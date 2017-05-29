package com.cloud.ops.core.resource;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.resource.repository.ResourcePackageConfigRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResourcePackageConfigService {
    @Autowired
    private ResourcePackageConfigRepository dao;

    public ResourcePackageConfig get(String id) {
        return dao.findOne(id);
    }

    public ResourcePackageConfig findByApplicationId(@NonNull String appId) {
        return dao.findByApplicationId(appId);
    }

    public ResourcePackageConfig create(ResourcePackageConfig version) {
        dao.save(version);
        return version;
    }
    public void delete(String id) {
        dao.delete(id);
    }

    public ResourcePackageConfig update(ResourcePackageConfig version) {
        ResourcePackageConfig db = this.get(version.getId());
        BeanUtils.copyNotNullProperties(version, db);
        dao.save(db);
        return db;
    }
}
