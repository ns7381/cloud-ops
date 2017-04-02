package com.cloud.ops.service;

import com.cloud.ops.dao.WorkFlowDao;
import com.cloud.ops.entity.WorkFlow;

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
public class WorkFlowService {
	
	@Autowired
	private WorkFlowDao dao;

	public WorkFlow get(String id) {
		return dao.findOne(id);
	}

	public WorkFlow save(WorkFlow workFlow){
		dao.save(workFlow);
		return workFlow;
	}

    public List<WorkFlow> getByObjectId(String objectId) {
        return dao.findByObjectId(objectId);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public WorkFlow update(WorkFlow workFlow){
        Assert.notNull(workFlow.getId());
        WorkFlow db = this.get(workFlow.getId());
        BeanUtils.copyNotNullProperties(workFlow, db);
        dao.save(db);
        return db;
	}

}
