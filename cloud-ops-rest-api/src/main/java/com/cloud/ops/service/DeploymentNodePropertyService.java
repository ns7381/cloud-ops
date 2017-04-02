package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodePropertyDao;
import com.cloud.ops.entity.deployment.DeploymentNodeProperty;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class DeploymentNodePropertyService {
	
	@Autowired
	private DeploymentNodePropertyDao dao;

	public DeploymentNodeProperty get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNodeProperty create(DeploymentNodeProperty deploymentNodeProperty){
		dao.save(deploymentNodeProperty);
		return deploymentNodeProperty;
	}

    public List<DeploymentNodeProperty> getByNodeId(String nodeId) {
        return dao.findByDeploymentNodeId(nodeId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNodeProperty update(DeploymentNodeProperty deploymentNodeProperty){
        Assert.notNull(deploymentNodeProperty.getId());
        DeploymentNodeProperty db = this.get(deploymentNodeProperty.getId());
        BeanUtils.copyNotNullProperties(deploymentNodeProperty, db);
        dao.save(db);
        return db;
	}

}
