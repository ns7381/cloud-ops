package com.cloud.ops.service;

import com.cloud.ops.dao.TopologyArchiveDao;
import com.cloud.ops.entity.deployment.TopologyArchive;
import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class TopologyArchiveService {
	
	@Autowired
	private TopologyArchiveDao dao;

	public TopologyArchive get(String id) {
		return dao.findOne(id);
	}

	public TopologyArchive create(TopologyArchive entity){
		dao.save(entity);
		return entity;
	}

    public List<TopologyArchive> findByTopologyId(String topologyId) {
        return dao.findByTopologyId(topologyId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public TopologyArchive update(TopologyArchive entity){
        Assert.notNull(entity.getId());
        TopologyArchive db = this.get(entity.getId());
        BeanUtils.copyNotNullProperties(entity, db);
        dao.save(db);
        return db;
	}

}
