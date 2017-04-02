package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodeArtifactDao;
import com.cloud.ops.entity.deployment.DeploymentNodeArtifact;
import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class DeploymentNodeArtifactService {
	
	@Autowired
	private DeploymentNodeArtifactDao dao;

	public DeploymentNodeArtifact get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNodeArtifact create(DeploymentNodeArtifact deploymentNodeArtifact){
		dao.save(deploymentNodeArtifact);
		return deploymentNodeArtifact;
	}

    public List<DeploymentNodeArtifact> getByNodeId(String nodeId) {
        return dao.findByDeploymentNodeId(nodeId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNodeArtifact update(DeploymentNodeArtifact deploymentNodeArtifact){
        Assert.notNull(deploymentNodeArtifact.getId());
        DeploymentNodeArtifact db = this.get(deploymentNodeArtifact.getId());
        BeanUtils.copyNotNullProperties(deploymentNodeArtifact, db);
        dao.save(db);
        return db;
	}

}
