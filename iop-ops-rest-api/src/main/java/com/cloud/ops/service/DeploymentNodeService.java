package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentNodeDao;
import com.cloud.ops.entity.deployment.DeploymentNode;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class DeploymentNodeService {
	
	@Autowired
	private DeploymentNodeDao dao;

	public DeploymentNode get(String id) {
		return dao.findOne(id);
	}

	public DeploymentNode create(DeploymentNode shell){
		dao.save(shell);
		return shell;
	}

    public List<DeploymentNode> getByTopologyId(String topologyId) {
        return dao.findByDeploymentTopologyId(topologyId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentNode update(DeploymentNode shell){
        Assert.notNull(shell.getId());
        DeploymentNode db = this.get(shell.getId());
        BeanUtils.copyNotNullProperties(shell, db);
        dao.save(db);
        return db;
	}

}
