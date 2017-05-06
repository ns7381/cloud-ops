package com.cloud.ops.service;

import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.entity.Resource.ResourcePackageType;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.entity.topology.TopologyArchive;
import com.cloud.ops.entity.topology.TopologyArchiveType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.LockModeType;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@CacheConfig(cacheNames = "applications")
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
    private ResourcePackageConfigService resourcePackageConfigService;
    @Autowired
    private TopologyService topologyService;
    @Autowired
    private TopologyArchiveService topologyArchiveService;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private ApplicationContext context;

    @Cacheable(key = "#id")
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
            nodeTemplate.declaredAttributes().put("hosts", TypeList.instance(TypeString.instance()).instantiate(localLocation.getHosts()));
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
        return dao.findByEnvironmentId(environmentId, SortConstant.CREATED_AT);
    }

    public void delete(String id) {
        for (ResourcePackage resourcePackage : resourcePackageService.findByApplicationId(id)) {
            resourcePackageService.delete(resourcePackage.getId());
        }
        ResourcePackageConfig resourcePackageConfig = resourcePackageConfigService.findByApplicationId(id);
        if (resourcePackageConfig != null) {
            resourcePackageConfigService.delete(resourcePackageConfig.getId());
        }
        dao.delete(id);
    }

    public Application update(Application app) {
        Assert.notNull(app.getId(), "app id can not be null");
        Application db = dao.findOne(app.getId());
        BeanUtils.copyNotNullProperties(app, db);
        dao.save(db);
        return db;
    }

    @Lock(LockModeType.READ)
    public Boolean deploy(String id, String nodeId, String packageId) {
        Application application = this.get(id);
        ResourcePackage resourcePackage = resourcePackageService.get(packageId);
        List<TopologyArchive> archives = topologyArchiveService.findByTopologyId(application.getTopologyId());
        Map<String, String> archiveMap = archives.stream().collect(Collectors.toMap(TopologyArchive::getName, TopologyArchive::getFilePath));
        archiveMap.put(TopologyArchiveType.PATCH_PACKAGE.name(), resourcePackage.getWarPath());
        String interfaceName = (resourcePackage.getType().equals(ResourcePackageType.PATCH) ?
                DeploymentType.PATCH_DEPLOY : DeploymentType.WAR_DEPLOY).toString();
        Map<String, DeploymentNode> nodeMap =
                application.getNodes().stream().collect(Collectors.toMap(DeploymentNode::getName,
                        Function.identity()));
        WorkFlow workFlow = new WorkFlow();
        workFlow.setName(interfaceName);
        workFlow.setStartAt(new Date());
        workFlow.setObjectId(application.getId());
        workFlow.setPackageId(packageId);
        workFlowService.save(workFlow);
        List<WorkFlowStep> stepTemps = processInterface(nodeId, interfaceName, nodeMap, archiveMap);
        List<WorkFlowStep> steps = Lists.newArrayList();
        for (WorkFlowStep stepTemp : stepTemps) {
            for (String hostIp : stepTemp.getLocation().getHosts()) {
                WorkFlowStep step = new WorkFlowStep();
                BeanUtils.copyNotNullProperties(stepTemp, step);
                step.setHostIp(hostIp.trim());
                step.setWorkFlowId(workFlow.getId());
                workFlowStepService.save(step);
                steps.add(step);
            }
        }
        new WorkFlowTask(context, workFlow, steps).start();
        return Boolean.TRUE;
    }

    private List<WorkFlowStep> processInterface(String nodeName, String interfaceName, Map<String, DeploymentNode> nodeMap,
                                                Map<String, String> archiveMap) {
        List<WorkFlowStep> results = Lists.newArrayList();
        WorkFlowStep doInterfaceTemplate = new WorkFlowStep();
        doInterfaceTemplate.setName(interfaceName);
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
                    results.addAll(processInterface(doNodeName, doInterfaceName, nodeMap, archiveMap));
                });
            }
        }

        //2 process host requirement
        processHostRequirement(doNode, nodeMap, doInterfaceTemplate);

        //3 process inputs
        processInputs(nodeName, doInterface, nodeMap, doInterfaceTemplate, archiveMap);

        //4 process implement
        TopologyArchive archive = new TopologyArchive();
        archive.setName(doInterface.getImplementation());
        archive.setType(TopologyArchiveType.SCRIPT);
        archive.setFilePath(archiveMap.get(doInterface.getImplementation()));
        doInterfaceTemplate.getArchives().add(archive);

        results.add(doInterfaceTemplate);
        return results;
    }

    private void processInputs(String nodeName, Interface doInterface, Map<String, DeploymentNode> nodeMap,
                               WorkFlowStep doInterfaceTemplate, Map<String, String> archiveMap) {
        Map<String, String> ENVMap = Maps.newHashMap();
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
                ENVMap.put(inputMap.getKey(), ARTIFACT_PATH + "/" + artifact.getFile());
                if ("tosca.artifacts.PatchFile".equals(artifact.getType())) {
                    TopologyArchive archive = new TopologyArchive();
                    archive.setName(artifact.getFile());
                    archive.setType(TopologyArchiveType.PATCH_PACKAGE);
                    archive.setFilePath(archiveMap.get(TopologyArchiveType.PATCH_PACKAGE.name()));
                    doInterfaceTemplate.getArchives().add(archive);
                }
            }
        }
        doInterfaceTemplate.setEnv(ENVMap);
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
        doInterfaceTemplate.setLocation(LocalLocation.builder().user((String) attributes.get("user"))
                .password((String) attributes.get("password")).hosts((List<String>) attributes.get("hosts")).build());
    }

    @CacheEvict(key = "#id")
    public Boolean changeApplicationAttributes(String id, String nodeId, Map<String, Object> attributes) {
        String yamlFilePath = dao.getOne(id).getYamlFilePath();
        topologyService.updateAttribute(yamlFilePath, nodeId, attributes);
        return Boolean.TRUE;
    }
}
