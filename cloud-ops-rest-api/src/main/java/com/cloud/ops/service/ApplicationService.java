package com.cloud.ops.service;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.entity.application.Application;
import com.cloud.ops.entity.application.ApplicationEnvironment;
import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.entity.topology.TopologyArchive;
import com.cloud.ops.esc.LocationServiceProvider;
import com.cloud.ops.esc.local.LocalLocation;
import com.cloud.ops.repository.ApplicationRepository;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.toscamodel.Tosca;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.cloud.ops.toscamodel.wf.WorkFlow;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.LockModeType;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cloud.ops.store.FileStore.TOPOLOGY_FILE_PATH;

@Service
@Transactional
@CacheConfig(cacheNames = "applications")
public class ApplicationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationRepository dao;
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
    private LocationServiceProvider locationServiceProvider;
    @Autowired
    private ApplicationEnvironmentService applicationEnvironmentService;

    @Cacheable(key = "#id")
    public Application get(String id) {
        Application application = dao.findOne(id);
        TopologyContext context = Tosca.newEnvironment(application.getYamlFilePath()).getTopologyContext();
        application.setTopologyContext(context);
        return application;
    }

    public Application create(Application app) {
        Topology topology = topologyService.get(app.getTopologyId());
        ApplicationEnvironment appEnv = applicationEnvironmentService.get(app.getEnvironmentId());
        WorkFlow install = Tosca.newEnvironment(topology.getYamlFilePath()).getWorkFlow("install");
        Map<String, WorkFlow> workFlows = Maps.newHashMap();
        workFlows.put("install", install);
        topology.getTopologyContext().setWorkFlowMap(workFlows);
        TopologyContext topologyContext = locationServiceProvider.install(topology.getTopologyContext(),
                generateLocation(appEnv, app.getLocation()));

        String fileName = fileStore.makeFile(TOPOLOGY_FILE_PATH + app.getEnvironmentId()
                + File.separator + app.getName() + File.separator) + "topology.yaml";
        Tosca.newEnvironment(topology.getYamlFilePath()).updateAttribute(topologyContext, fileName);
        app.setYamlFilePath(fileName);

        dao.save(app);
        return app;
    }

    private LocalLocation generateLocation(ApplicationEnvironment appEnvironment, LocalLocation location) {
        if ("local".equals(appEnvironment.getType())) {
            location.setLocationType("local");
            return location;
        }
        //TODO docker location generate
        return null;
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
        WorkFlow patch_deploy = Tosca.newEnvironment(application.getYamlFilePath()).getWorkFlow("patch_deploy");
        patch_deploy.setObjectId(id);
        Map<String, WorkFlow> workFlows = Maps.newHashMap();
        workFlows.put("patch_deploy", patch_deploy);
        application.getTopologyContext().setWorkFlowMap(workFlows);
        ApplicationEnvironment appEnv = applicationEnvironmentService.get(application.getEnvironmentId());
        List<TopologyArchive> archives = topologyArchiveService.findByTopologyId(application.getTopologyId());
        Map<String, String> archiveMap = archives.stream().collect(Collectors.toMap(TopologyArchive::getName, TopologyArchive::getFilePath));
        ResourcePackage resourcePackage = resourcePackageService.get(packageId);
        archiveMap.put("patch_package.zip", resourcePackage.getWarPath());
        LocalLocation location = generateLocation(appEnv, application.getLocation());
        location.getMetaProperties().putAll(archiveMap);
        locationServiceProvider.executeWorkFlow(application.getTopologyContext(), location);

        return Boolean.TRUE;
    }

    @CacheEvict(key = "#id")
    public Boolean changeApplicationAttributes(String id, String nodeId, Map<String, Object> attributes) {
        Application application = dao.findOne(id);
        Tosca.newEnvironment(application.getYamlFilePath()).updateAttribute(nodeId, attributes, application.getYamlFilePath());
        return Boolean.TRUE;
    }
}
