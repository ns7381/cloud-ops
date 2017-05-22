package com.cloud.ops.core.service;

import com.cloud.ops.dao.modal.SortConstant;
import com.cloud.ops.entity.workflow.WorkFlowStep;
import com.cloud.ops.repository.WorkFlowStepRepository;
import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class WorkFlowStepService {
	
	@Autowired
	private WorkFlowStepRepository dao;

	public WorkFlowStep get(String id) {
		return dao.findOne(id);
	}

	public WorkFlowStep save(WorkFlowStep entity){
		dao.save(entity);
		return entity;
	}

    public List<WorkFlowStep> findByWorkFlowId(String workFlowId) {
        return dao.findByWorkFlowId(workFlowId, SortConstant.CREATED_AT);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public WorkFlowStep update(WorkFlowStep entity){
        Assert.notNull(entity.getId());
        WorkFlowStep db = this.get(entity.getId());
        BeanUtils.copyNotNullProperties(entity, db);
        dao.save(db);
        return db;
	}

}
