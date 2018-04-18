package com.cloud.ops.core.resource;

import com.cloud.ops.common.utils.AESUtil;
import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.core.model.Resource.ResourcePackageFile;
import com.cloud.ops.core.resource.repository.ResourcePackageFileRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ResourcePackageFileService {
    @Value("${cloud-ops.file.package}")
    private String PACKAGE_FILE_PATH;
    @Autowired
    private ResourcePackageFileRepository dao;

    public ResourcePackageFile get(String id) {
        return dao.findOne(id);
    }

    public List<ResourcePackageFile> findByApplicationId(@NonNull String appId) {
        return dao.findByApplicationId(appId);
    }

    public ResourcePackageFile create(ResourcePackageFile entity) {
        switch (entity.getType()) {
            case MD5:
                break;
            case CIPHER:
                String uploadPath = PACKAGE_FILE_PATH + File.separator + UUID.randomUUID().toString() + File.separator;
                entity.setPath(AESUtil.createKey(entity.getSeed(), uploadPath + new File(entity.getName()).getName()));
                break;
        }
        dao.save(entity);
        return entity;
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public ResourcePackageFile update(ResourcePackageFile entity) {
        ResourcePackageFile db = this.get(entity.getId());
        BeanUtils.copyNotNullProperties(entity, db);
        dao.save(db);
        return db;
    }
}
