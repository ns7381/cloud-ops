package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentDao;
import com.cloud.ops.entity.Location.LocationLocal;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.WorkFlow;
import com.cloud.ops.entity.deployment.*;
import com.cloud.ops.utils.*;
import com.cloud.ops.ws.WebSocketConstants;
import com.cloud.ops.ws.WebSocketHandler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeploymentService {

    private static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private static final String INTERFACE_PATH = "/opt/iop-ops/interface";
    @Autowired
    private DeploymentDao dao;
    @Autowired
    private WebSocketHandler webSocketHandler;
    @Autowired
    private DeploymentNodeInterfaceService deploymentNodeInterfaceService;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private DeploymentNodeArtifactService deploymentNodeArtifactService;
    @Autowired
    private DeploymentNodeRequirementService deploymentNodeRequirementService;
    @Autowired
    private DeploymentNodePropertyService deploymentNodePropertyService;
    @Autowired
    private DeploymentNodeService deploymentNodeService;
    @Autowired
    private LocationLocalService locationLocalService;
    @Autowired
    private ResourcePackageService resourcePackageService;

    public Deployment get(String id) {
        return dao.findOne(id);
    }

    public Deployment create(Deployment deployment) {
        dao.save(deployment);
        return deployment;
    }

    public List<Deployment> getAll() {
        return dao.findAll();
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public Deployment update(Deployment shell) {
        Assert.notNull(shell.getId());
        Deployment db = this.get(shell.getId());
        BeanUtils.copyNotNullProperties(shell, db);
        dao.save(db);
        return db;
    }

    public Boolean doInterface(String id, String interfaceId, Map<String, Object> params) {
        Deployment deployment = this.get(id);
        WorkFlow workFlow = new WorkFlow();
        workFlow.setStartAt(new Date());
        workFlow.setMessage("准备...\n");
        workFlow.setObjectId(deployment.getId());
        workFlow.setStep("准备中");
        DeploymentNodeInterface nodeInterface = deploymentNodeInterfaceService.getWithInputs(interfaceId);
        workFlow.setName(nodeInterface.getName());
        workFlowService.save(workFlow);

        Map<String, String> localFileAndRemoteFileNameMap = new HashMap<>();
        StringBuilder shellContent = new StringBuilder();
        String deploymentTopologyId = deployment.getDeploymentTopologyId();

        //process interface's input params
        processInterfaceInputParams(interfaceId, deploymentTopologyId, localFileAndRemoteFileNameMap, shellContent, workFlow, params);


        //remote connect target host to transfer file and action shell script
        final String shellLocalPath = nodeInterface.getImplementation();
        final String shellScript = shellContent.toString();
        final Map<String, String> fileMap = localFileAndRemoteFileNameMap;
        DeploymentNodeRequirement requirement = deploymentNodeRequirementService.getByNodeIdAndName(nodeInterface.getDeploymentNodeId(), "HOST");

        final List<LocationLocal> hosts = locationLocalService.getByLocationIdAndType(deployment.getLocationId(), NodeType.valueOf(requirement.getValue()));
        new ThreadWithEntity<WorkFlow>(workFlow){

            @Override
            public void run(WorkFlow workFlow) {
                WorkFlowService service = SpringContextHolder.getBean(WorkFlowService.class);
                try {
                    Boolean isSuccess = true;
                    for (LocationLocal host : hosts) {
                        RemoteExecuteCommand remoteExecuteCommand = new RemoteExecuteCommand(host.getIp(), host.getUsername(), host.getPassword());
                        remoteExecuteCommand.execute("mkdir -p " + INTERFACE_PATH+";mkdir -p "+ARTIFACT_PATH);
                        workFlow.setStep("上传文件【" + host.getIp() + "】");
                        workFlow.appendMessage("上传文件【" + host.getIp() + "】\n");
                        service.save(workFlow);
                        webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
                        SCPUtils.uploadFilesToServer(host, shellLocalPath, INTERFACE_PATH, "0744");
                        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                            SCPUtils.uploadFilesToServer(host, entry.getKey(), entry.getValue(), ARTIFACT_PATH, "0644");
                        }
                        workFlow.setStep("执行脚本【" + host.getIp() + "】");
                        workFlow.appendMessage("执行脚本【" + host.getIp() + "】");
                        service.save(workFlow);
                        webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
                        RemoteExecuteResult executeResult = remoteExecuteCommand.execute(shellScript.toString());
                        String message = executeResult.getMessage();
                        message = message.replaceAll("\\s+\\d+K\\s+\\d+\\.?\\d*(M|K)?\\n\\r", "");
                        if(message.length() > 65534){
                            message = message.substring(message.length()-65534);
                        }
                        if (executeResult.getExitCode() == null) {
                            workFlow.appendMessage("完成。" + message + "\n");
                        } else if (executeResult.getExitCode() == 0) {
                            workFlow.appendMessage("成功"+ message +"\n");
                        } else {
                            workFlow.appendMessage("失败。原因：" + message + "\n");
                            isSuccess = false;
                        }
                        service.save(workFlow);
                    }
                    workFlow.setStep(isSuccess ? "SUCCESS" : "FAIL");
                    workFlow.appendMessage("完成\n");
                    workFlow.setEndAt(new Date());
                    service.save(workFlow);
                    webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
                } catch (IOException e) {
                    e.printStackTrace();
                    workFlow.setStep("FAIL");
                    workFlow.appendMessage("异常。\n原因:" + e.getMessage());
                    workFlow.setEndAt(new Date());
                    service.save(workFlow);
                    webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
                }

            }
        }.start();

        return Boolean.TRUE;
    }

    private void processInterfaceInputParams(String interfaceId, String deploymentTopologyId,
                                             Map<String, String> localFileAndRemoteFileNameMap,
                                             StringBuilder shellContent, WorkFlow workFlow, Map<String, Object> params) {
        DeploymentNodeInterface nodeInterface;List<DeploymentNode> deploymentNodes = deploymentNodeService.getByTopologyId(deploymentTopologyId);
        Map<String, String> propertyKV = new HashMap<>();
        for (DeploymentNode deploymentNode : deploymentNodes) {
            List<DeploymentNodeProperty> nodeProperties = deploymentNodePropertyService.getByNodeId(deploymentNode.getId());
            for (DeploymentNodeProperty nodeProperty : nodeProperties) {
                propertyKV.put(deploymentNode.getName() + ", " + nodeProperty.getName(), nodeProperty.getValue());
            }
        }
        nodeInterface = deploymentNodeInterfaceService.getWithInputs(interfaceId);
        workFlow.setName(nodeInterface.getName());
        List<DeploymentNodeArtifact> artifacts = deploymentNodeArtifactService.getByNodeId(nodeInterface.getDeploymentNodeId());

        for (DeploymentNodeInterfaceInput input : nodeInterface.getInterfaceInputs()) {
            String inputValue = (String)params.get(input.getName());
            switch (input.getType()) {
                case ARTIFACT:
                    ResourcePackage resourcePackage = resourcePackageService.get(inputValue);
                    for (DeploymentNodeArtifact artifact : artifacts) {
                        if (artifact.getName().equals(input.getValue())) {
                            localFileAndRemoteFileNameMap.put(resourcePackage.getWarPath(), artifact.getPath());
                            shellContent.append("export "+input.getName()+"=" + artifact.getPath() + ";");
                            workFlow.setDescription(artifact.getName()+"部署为版本:【"+resourcePackage.getVersion()+"】");
                        }
                    }
                    break;
                case PROPERTY:
                    if (StringUtils.isNotBlank(inputValue)) {
                        shellContent.append("export " + input.getName() + "=" + inputValue + ";");
                    } else {
                        shellContent.append("export "+input.getName()+"=" + propertyKV.get(input.getValue()) + ";");
                    }
                    break;
                case STRING:
                    shellContent.append("export "+input.getName()+"=" + inputValue + ";");
                    break;
            }
        }
        String shellName = new File(nodeInterface.getImplementation()).getName();
        shellContent.append("sh " + INTERFACE_PATH +"/"+ shellName);
    }
}
