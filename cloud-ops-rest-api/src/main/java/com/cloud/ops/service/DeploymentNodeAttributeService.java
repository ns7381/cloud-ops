package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodeAttributeDao;
import com.cloud.ops.entity.deployment.DeploymentNodeAttribute;
import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class DeploymentNodeAttributeService {
	
	@Autowired
	private DeploymentNodeAttributeDao dao;

	public DeploymentNodeAttribute get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNodeAttribute create(DeploymentNodeAttribute deploymentNodeAttribute){
		dao.save(deploymentNodeAttribute);
		return deploymentNodeAttribute;
	}

    public List<DeploymentNodeAttribute> getByNodeId(String nodeId) {
        return dao.findByDeploymentNodeId(nodeId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNodeAttribute update(DeploymentNodeAttribute deploymentNodeAttribute){
        Assert.notNull(deploymentNodeAttribute.getId());
        DeploymentNodeAttribute db = this.get(deploymentNodeAttribute.getId());
        BeanUtils.copyNotNullProperties(deploymentNodeAttribute, db);
        dao.save(db);
        return db;
	}

}
