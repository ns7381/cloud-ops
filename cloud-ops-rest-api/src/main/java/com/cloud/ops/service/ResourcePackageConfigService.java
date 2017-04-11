package com.cloud.ops.service;

import com.cloud.ops.configuration.ws.CustomWebSocketHandler;
import com.cloud.ops.configuration.ws.WebSocketConstants;
import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.entity.Resource.ResourcePackageStatus;
import com.cloud.ops.repository.ResourcePackageConfigRepository;
import com.cloud.ops.utils.*;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
