package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodeRequirementDao;
import com.cloud.ops.entity.deployment.DeploymentNodeRequirement;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeploymentNodeRequirementService {
	
	@Autowired
	private DeploymentNodeRequirementDao dao;

	public DeploymentNodeRequirement get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNodeRequirement create(DeploymentNodeRequirement deploymentNodeRequirement){
		dao.save(deploymentNodeRequirement);
		return deploymentNodeRequirement;
	}

    public List<DeploymentNodeRequirement> getByNodeId(String nodeId) {
        return dao.findByDeploymentNodeId(nodeId);
    }

    public DeploymentNodeRequirement getByNodeIdAndName(String nodeId, String name) {
        return dao.findByDeploymentNodeIdAndName(nodeId, name);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNodeRequirement update(DeploymentNodeRequirement deploymentNodeRequirement){
        Assert.notNull(deploymentNodeRequirement.getId());
        DeploymentNodeRequirement db = this.get(deploymentNodeRequirement.getId());
        BeanUtils.copyNotNullProperties(deploymentNodeRequirement, db);
        dao.save(db);
        return db;
	}

}
