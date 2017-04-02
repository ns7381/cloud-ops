package com.cloud.ops.service;

import com.cloud.ops.dao.DeploymentTopologyDao;
import com.cloud.ops.entity.deployment.DeploymentTopology;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class DeploymentTopologyService {
	
	@Autowired
	private DeploymentTopologyDao dao;

	public DeploymentTopology get(String id) {
		return dao.findOne(id);
	}

	public DeploymentTopology create(DeploymentTopology shell){
		dao.save(shell);
		return shell;
	}

    public List<DeploymentTopology> getAll() {
        return dao.findAll();
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public DeploymentTopology update(DeploymentTopology shell){
        Assert.notNull(shell.getId());
        DeploymentTopology db = this.get(shell.getId());
        BeanUtils.copyNotNullProperties(shell, db);
        dao.save(db);
        return db;
	}
}
