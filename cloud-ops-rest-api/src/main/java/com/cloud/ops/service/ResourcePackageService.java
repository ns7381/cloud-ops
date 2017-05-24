package com.cloud.ops.service;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.repository.ResourcePackageRepository;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cloud.ops.configuration.ws.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ResourcePackageService {
    @Autowired
    private ResourcePackageRepository dao;

    public ResourcePackage get(String id) {
        return dao.findOne(id);
    }

    public ResourcePackage create(ResourcePackage version) {
        dao.save(version);
        return version;
    }
    public void delete(String id) {
        try {
            String warPath = this.get(id).getWarPath();
            if (StringUtils.isNotBlank(warPath)) {
                org.apache.commons.io.FileUtils.forceDeleteOnExit(new File(warPath));
            }
            dao.delete(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<ResourcePackage> findByApplicationId(String applicationId) {
        return dao.findByApplicationId(applicationId, SortConstant.CREATED_AT);
    }

    public ResourcePackage update(ResourcePackage version) {
        ResourcePackage db = this.get(version.getId());
        BeanUtils.copyNotNullProperties(version, db);
        dao.save(db);
        return db;
    }

    public void updateDeployStatus(String packageId) {
        for (ResourcePackage resourcePackage : dao.findAll()) {
            if (ResourcePackageStatus.DEPLOYED.equals(resourcePackage.getStatus())) {
                resourcePackage.setStatus(ResourcePackageStatus.FINISH);
                dao.save(resourcePackage);
            }
        }
        ResourcePackage db = dao.findOne(packageId);
        db.setStatus(ResourcePackageStatus.DEPLOYED);
        dao.save(db);
    }
}
