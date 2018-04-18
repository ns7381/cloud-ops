package com.cloud.ops.core.application;

import com.cloud.ops.common.exception.OpsException;
import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.common.utils.FileHelper;
import com.cloud.ops.core.application.repository.ApplicationRepository;
import com.cloud.ops.core.model.Resource.ResourcePackage;
import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.model.application.Application;
import com.cloud.ops.core.model.topology.TopologyArchive;
import com.cloud.ops.core.model.topology.TopologyEntity;
import com.cloud.ops.core.resource.ResourcePackageConfigService;
import com.cloud.ops.core.resource.ResourcePackageService;
import com.cloud.ops.core.topology.TopologyArchiveService;
import com.cloud.ops.core.topology.TopologyService;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.esc.LocationMatcher;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.tosca.Tosca;
import com.cloud.ops.tosca.model.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.LockModeType;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@CacheConfig(cacheNames = "applications")
public class ApplicationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${cloud-ops.file.topology}")
    private String TOPOLOGY_FILE_PATH;
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
    private LocationMatcher locationProvider;

    @Cacheable(key = "#id")
    public Application get(String id) {
        Application application = dao.findOne(id);
        Topology context = Tosca.read(application.getYamlFilePath());
        application.setTopology(context);
        return application;
    }

    public Application create(Application app) {
        dao.save(app);
        TopologyEntity entity = topologyService.get(app.getTopologyId());
        WorkFlowEntity wf = new WorkFlowEntity();
        wf.setStartAt(new Date());
        wf.setName("install");
        wf.setObjectId(app.getId());
        List<TopologyArchive> archives = topologyArchiveService.findByTopologyId(app.getTopologyId());
        Map<String, Object> archiveMap = archives.stream().collect(Collectors.toMap(TopologyArchive::getName, TopologyArchive::getFilePath));
        app.getInputs().putAll(archiveMap);
        Topology topology = locationProvider.install(Tosca.read(entity.getYamlFilePath()), wf, app.getInputs());

        String fileName = TOPOLOGY_FILE_PATH + File.separator + UUID.randomUUID() + File.separator + "topology.yaml";
        if (!FileHelper.createFile(fileName)) {
            throw new OpsException("yaml file create error");
        }
        Tosca.write(topology, fileName);
        app.setYamlFilePath(fileName);
        dao.save(app);
        return app;
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
        Application app = this.get(id);
        Topology topology = Tosca.read(app.getYamlFilePath());
        List<TopologyArchive> archives = topologyArchiveService.findByTopologyId(app.getTopologyId());
        Map<String, Object> archiveMap = archives.stream().collect(Collectors.toMap(TopologyArchive::getName, TopologyArchive::getFilePath));
        ResourcePackage resourcePackage = resourcePackageService.get(packageId);
        archiveMap.put("patch_package.zip", resourcePackage.getWarPath());
        WorkFlowEntity entity = new WorkFlowEntity();
        entity.setStartAt(new Date());
        entity.setName("patch_deploy");
        entity.setNodeName(nodeId);
        entity.setObjectId(id);
        entity.setPackageId(packageId);
        locationProvider.executeWorkFlow(topology, entity, archiveMap);
        return Boolean.TRUE;
    }

    @CacheEvict(key = "#id")
    public Boolean changeApplicationAttributes(String id, String nodeId, Map<String, Object> attributes) {
        Application app = dao.findOne(id);
        Topology topology = Tosca.read(app.getYamlFilePath());
        topology.updateAttributes(nodeId, attributes);
        Tosca.write(topology, app.getYamlFilePath());
        return Boolean.TRUE;
    }
}
