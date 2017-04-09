package com.cloud.ops.service;

import com.cloud.ops.repository.ResourcePackageRepository;
import com.cloud.ops.entity.Resource.ResourcePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResourcePackageServiceTool {
	@Autowired
	private ResourcePackageRepository dao;
	public ResourcePackage save(ResourcePackage db){
        dao.save(db);
        return db;
	}
}
