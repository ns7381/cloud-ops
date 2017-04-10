package com.cloud.ops.service;

import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.repository.ApplicationRepository;
import com.cloud.ops.entity.application.WorkFlow;
import com.cloud.ops.entity.application.*;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;
import com.cloud.ops.toscamodel.basictypes.impl.TypeList;
import com.cloud.ops.toscamodel.basictypes.impl.TypeString;
import com.cloud.ops.toscamodel.impl.ToscaEnvironment;
import com.cloud.ops.utils.*;
import com.cloud.ops.configuration.ws.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private static final String INTERFACE_PATH = "/opt/iop-ops/interface";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ApplicationRepository dao;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private ResourcePackageService resourcePackageService;
    @Autowired
    private TopologyService topologyService;
    @Autowired
    private FileStore fileStore;

    public Application get(String id) {
        Application application = dao.findOne(id);
        IToscaEnvironment toscaEnvironment = Tosca.newEnvironment();
        try {
            toscaEnvironment.readFile(new FileReader(application.getYamlFilePath()), false);
            application.setToscaEnvironment(toscaEnvironment);
            INodeType rootNode = (INodeType) toscaEnvironment.getNamedEntity("tosca.nodes.Root");
            Iterable<INodeTemplate> rootNodeTemplate = toscaEnvironment.getNodeTemplatesOfType(rootNode);
            List<DeploymentNode> nodes = new ArrayList<>();
            for (INodeTemplate nodeTemplate : rootNodeTemplate) {
                nodes.add(DeploymentNode.convert(nodeTemplate));
            }
            application.setNodes(nodes);
        } catch (FileNotFoundException e) {
            logger.error("yaml file not find. ", e);
        }
        return application;
    }

    public Application create(Application application) {
        Topology topology = topologyService.get(application.getTopologyId());
        IToscaEnvironment toscaEnvironment = topology.getToscaEnvironment();
        INodeType rootNode = (INodeType) toscaEnvironment.getNamedEntity("tosca.nodes.Compute");
        Iterable<INodeTemplate> rootNodeTemplate = toscaEnvironment.getNodeTemplatesOfType(rootNode);
        for (INodeTemplate nodeTemplate : rootNodeTemplate) {
            LocalLocation localLocation = application.getLocations().get(nodeTemplate.toString());
            assert localLocation != null;
            assert StringUtils.isNotBlank(localLocation.getHosts());
            List<String> hosts = Arrays.asList(localLocation.getHosts().split(",")).stream().map(String::trim).collect(Collectors.toList());
            nodeTemplate.declaredAttributes().put("hosts", TypeList.instance(TypeString.instance()).instantiate(hosts));
            nodeTemplate.declaredAttributes().put("user", TypeString.instance().instantiate(localLocation.getUser()));
            nodeTemplate.declaredAttributes().put("password", TypeString.instance().instantiate(localLocation.getPassword()));
        }
        try {
            String fileName = fileStore.makeFile(fileStore.TOPOLOGY_FILE_PATH +
                    topology.getName() + File.separator + application.getName() + File.separator) + "topology.yaml";
            toscaEnvironment.writeFile(new FileWriter(fileName));
            application.setYamlFilePath(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao.save(application);
        return application;
    }

    public List<Application> findByEnvironmentId(String environmentId) {
        return dao.findByEnvironmentId(environmentId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public Application update(Application app) {
        Assert.notNull(app.getId());
        Application db = dao.findOne(app.getId());
        BeanUtils.copyNotNullProperties(app, db);
        dao.save(db);
        return db;
    }

    public Boolean doInterface(String id, String interfaceId, Map<String, Object> params) {
        /*Application application = this.get(id);
        WorkFlow workFlow = new WorkFlow();
        workFlow.setStartAt(new Date());
        workFlow.setMessage("准备...\n");
        workFlow.setObjectId(application.getId());
        workFlow.setStep("准备中");
        DeploymentNodeInterface nodeInterface = deploymentNodeInterfaceService.getWithInputs(interfaceId);
        workFlow.setName(nodeInterface.getName());
        workFlowService.save(workFlow);

        Map<String, String> localFileAndRemoteFileNameMap = new HashMap<>();
        StringBuilder shellContent = new StringBuilder();
        String deploymentTopologyId = application.getDeploymentTopologyId();

        //process interface's input params
        processInterfaceInputParams(interfaceId, deploymentTopologyId, localFileAndRemoteFileNameMap, shellContent, workFlow, params);


        //remote connect target host to transfer file and action shell script
        final String shellLocalPath = nodeInterface.getImplementation();
        final String shellScript = shellContent.toString();
        final Map<String, String> fileMap = localFileAndRemoteFileNameMap;
        DeploymentNodeRequirement requirement = deploymentNodeRequirementService.getByNodeIdAndName(nodeInterface.getDeploymentNodeId(), "HOST");

        final List<LocationLocal> hosts = locationLocalService.getByLocationIdAndType(application.getLocationId(), NodeType.valueOf(requirement.getValue()));
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
        }.start();*/

        return Boolean.TRUE;
    }

    private void processInterfaceInputParams(String interfaceId, String deploymentTopologyId,
                                             Map<String, String> localFileAndRemoteFileNameMap,
                                             StringBuilder shellContent, WorkFlow workFlow, Map<String, Object> params) {
        /*DeploymentNodeInterface nodeInterface;List<DeploymentNode> deploymentNodes = deploymentNodeService.getByTopologyId(deploymentTopologyId);
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
        shellContent.append("sh " + INTERFACE_PATH +"/"+ shellName);*/
    }
}
