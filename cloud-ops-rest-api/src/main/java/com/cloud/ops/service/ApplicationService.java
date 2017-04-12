package com.cloud.ops.service;

import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageType;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.entity.workflow.WorkFlowStatus;
import com.cloud.ops.entity.workflow.WorkFlowStep;
import com.cloud.ops.repository.ApplicationRepository;
import com.cloud.ops.entity.workflow.WorkFlow;
import com.cloud.ops.entity.application.*;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.INodeType;
import com.cloud.ops.toscamodel.IToscaEnvironment;
import com.cloud.ops.toscamodel.Tosca;
import com.cloud.ops.toscamodel.basictypes.impl.TypeList;
import com.cloud.ops.toscamodel.basictypes.impl.TypeString;
import com.cloud.ops.toscamodel.impl.Artifact;
import com.cloud.ops.toscamodel.impl.Interface;
import com.cloud.ops.utils.*;
import com.cloud.ops.configuration.ws.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private static final String INTERFACE_PATH = "/opt/iop-ops/interface";

    @Autowired
    private ApplicationRepository dao;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private WorkFlowStepService workFlowStepService;
    @Autowired
    private ResourcePackageService resourcePackageService;
    @Autowired
    private TopologyService topologyService;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private ApplicationContext context;

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

    public Boolean deploy(String id, String nodeId, String packageId) {
        Application application = this.get(id);
        ResourcePackage resourcePackage = resourcePackageService.get(packageId);
        String interfaceName = resourcePackage.getType().equals(ResourcePackageType.PatchFile) ? "patch_deploy" : "war_deploy";
        Map<String, DeploymentNode> nodeMap =
                application.getNodes().stream().collect(Collectors.toMap(DeploymentNode::getName,
                        Function.identity()));
        List<WorkFlowStep> steps = processInterface(nodeId, interfaceName, nodeMap);
        for (WorkFlowStep step : steps) {
            workFlowStepService.save(step);
        }
        //deploy
        WorkFlow workFlow = WorkFlow.builder().name(interfaceName).startAt(new Date()).objectId(application.getId())
                .packageId(packageId).build();
        workFlowService.save(workFlow);
        new ThreadWithEntity<WorkFlow>(workFlow){

            @Override
            public void run(WorkFlow workFlow) {
                WorkFlowService service = context.getBean(WorkFlowService.class);
                WorkFlowStepService stepService = context.getBean(WorkFlowStepService.class);
                try {
                    //Traversal interface template to execute
                    for (WorkFlowStep executeObject : steps) {
                        workFlow.setStep(executeObject.getName());
                        String message;
                        for (Map<String, String> hostMap : executeObject.getLocations()) {
                            RemoteExecuteCommand remoteExecuteCommand = new RemoteExecuteCommand(hostMap.get("ip"),
                                    hostMap.get("user"), hostMap.get("password"));
                            remoteExecuteCommand.execute("mkdir -p " + INTERFACE_PATH + ";mkdir -p " + ARTIFACT_PATH);
                            SCPUtils.uploadFileToServer(hostMap.get("ip"), hostMap.get("user"),hostMap.get("password"),
                                    executeObject.getScriptFilePath(), INTERFACE_PATH, "0744");
                            for (Artifact artifact : executeObject.getArtifacts()) {
                                if (artifact.getType().equals("tosca.artifacts.PatchFile")) {
                                    SCPUtils.uploadFileToServer(hostMap.get("ip"), hostMap.get("user"),hostMap.get("password"),
                                            resourcePackage.getWarPath(), artifact.getFile(), ARTIFACT_PATH, "0644");
                                }
                            }
                            StringBuilder shellContent = new StringBuilder();
                            for (Map.Entry<String, Object> ENV : executeObject.getEnv().entrySet()) {
                                shellContent.append("export "+ENV.getKey()+"=" + ENV.getValue() + ";");
                            }
                            shellContent.append("sh " + INTERFACE_PATH +"/"+ new File(executeObject.getScriptFilePath()).getName());
                            RemoteExecuteResult executeResult = remoteExecuteCommand.execute(shellContent.toString());
                            message = executeResult.getMessage();
                            message = message.replaceAll("\\s+\\d+K\\s+\\d+\\.?\\d*(M|K)?\\n\\r", "");
                            if(message.length() > 65534){
                                message = message.substring(message.length()-65534);
                            }
                            executeObject.appendMessage(hostMap.get("ip") + " 执行结果： \n" + message);
                            if (executeResult.getExitCode() == null) {
                                executeObject.setStatus(WorkFlowStatus.SUCCESS);
                            } else if (executeResult.getExitCode() == 0) {
                                executeObject.setStatus(WorkFlowStatus.SUCCESS);
                            } else {
                                executeObject.setStatus(WorkFlowStatus.FAIL);
                            }
                        }
                        service.save(workFlow);
                        webSocketHandler.sendMsg(WebSocketConstants.WORKFLOW_STATUS, workFlow);
                    }
                } catch (IOException e) {

                }

            }
        }.start();
        return Boolean.TRUE;
    }

    private List<WorkFlowStep> processInterface(String nodeName, String interfaceName, Map<String, DeploymentNode> nodeMap) {
        List<WorkFlowStep> results = Lists.newArrayList();
        WorkFlowStep doInterfaceTemplate = new WorkFlowStep();
        DeploymentNode doNode = nodeMap.get(nodeName);
        assert doNode != null;
        Interface doInterface = doNode.getInterfaces().get(interfaceName);
        assert doInterface != null;
        //1 process dependency
        if (doInterface.getDependencies() != null && !doInterface.getDependencies().isEmpty()) {
            for (Map<String, Object> dependency : doInterface.getDependencies()) {
                dependency.entrySet().stream().filter(entry -> entry.getValue() instanceof Map).forEach(entry -> {
                    Map dependencyMap = (Map) entry.getValue();
                    List doInterfaces = (List) dependencyMap.get("do_interface");
                    String doNodeName = (String) doInterfaces.get(0);
                    doNodeName = doNodeName.equals("SELF") ? nodeName : doNodeName;
                    String doInterfaceName = (String) doInterfaces.get(1);
                    //process interface recursively
                    results.addAll(processInterface(doNodeName, doInterfaceName, nodeMap));
                });
            }
        }

        //2 process host requirement
        processHostRequirement(doNode, nodeMap, doInterfaceTemplate);

        //3 process inputs
        processInputs(nodeName, doInterface, nodeMap, doInterfaceTemplate);

        results.add(doInterfaceTemplate);
        return results;
    }

    private void processInputs(String nodeName, Interface doInterface, Map<String, DeploymentNode> nodeMap,
                               WorkFlowStep doInterfaceTemplate) {
        Map<String, Object> ENVMap = Maps.newHashMap();
        List<Artifact> artifacts = Lists.newArrayList();
        for (Map.Entry<String, Object> inputMap : doInterface.getInputs().entrySet()) {
            Map inputValueMap = (Map) inputMap.getValue();
            if (inputValueMap.get("get_attribute") != null) {
                List<String> list = (List<String>) inputValueMap.get("get_attribute");
                String applyNodeName = list.get(0).equals("SELF") ? nodeName : list.get(0);
                String applyNodeAttribute = list.get(1);
                String value = (String) nodeMap.get(applyNodeName).getAttributes().get(applyNodeAttribute);
                ENVMap.put(inputMap.getKey(), value);
            }
            if (inputValueMap.get("get_artifact") != null) {
                List<String> list = (List<String>) inputValueMap.get("get_artifact");
                String applyNodeName = list.get(0).equals("SELF") ? nodeName : list.get(0);
                String applyNodeArtifact = list.get(1);
                Artifact artifact = nodeMap.get(applyNodeName).getArtifacts().get(applyNodeArtifact);
                artifacts.add(artifact);
                ENVMap.put(inputMap.getKey(), ARTIFACT_PATH + "/" + artifact.getFile());
            }
        }
        doInterfaceTemplate.setEnv(ENVMap);
        doInterfaceTemplate.setArtifacts(artifacts);
    }


    private void processHostRequirement(DeploymentNode doNode, Map<String, DeploymentNode> nodeMap,
                                        WorkFlowStep doInterfaceTemplate) {
        List<Map<String, Object>> requirements = doNode.getRequirements();
        String hostNodeName = null;
        for (Map<String, Object> requirement : requirements) {
            if (requirement.get("host") != null) {
                hostNodeName = (String) requirement.get("host");
            }
        }
        assert hostNodeName != null;
        Map<String, Object> attributes = nodeMap.get(hostNodeName).getAttributes();
        List<String> ips = (List<String>) attributes.get("hosts");
        List<Map<String, String>> locations = Lists.newArrayList();
        for (String ip : ips) {
            Map<String, String> host = Maps.newHashMap();
            host.put("user", (String) attributes.get("user"));
            host.put("password", (String) attributes.get("password"));
            host.put("ip", ip);
            locations.add(host);
        }
        doInterfaceTemplate.setLocations(locations);
    }

    public Boolean changeApplicationAttributes(String id, String nodeId, Map<String, Object> attributes) {
        return null;
    }
}
