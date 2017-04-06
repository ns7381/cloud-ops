package com.cloud.ops.service;

import com.cloud.ops.dao.TopologyDao;
import com.cloud.ops.entity.deployment.Topology;
import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;


@Service
@Transactional
public class TopologyService {
	
	@Autowired
	private TopologyDao dao;

	public Topology get(String id) {
		return dao.findOne(id);
	}

	public Topology create(Topology shell){
		dao.save(shell);
		return shell;
	}

    public List<Topology> getAll() {
        return dao.findAll();
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public Topology update(String id, Topology topology){
        Assert.notNull(id, "id is required");
        Topology db = this.get(id);
        BeanUtils.copyNotNullProperties(topology, db);
        dao.save(db);
        return db;
	}
}
