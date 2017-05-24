package com.cloud.ops.service;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.repository.TopologyArchiveRepository;
import com.cloud.ops.entity.topology.TopologyArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class TopologyArchiveService {
	
	@Autowired
	private TopologyArchiveRepository dao;

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
        Assert.notNull(entity.getId(), "id is required");
        TopologyArchive db = this.get(entity.getId());
        BeanUtils.copyNotNullProperties(entity, db);
        dao.save(db);
        return db;
	}

}
