package com.cloud.ops.service;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.repository.WorkFlowRepository;
import com.cloud.ops.entity.workflow.WorkFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class WorkFlowService {
	
	@Autowired
	private WorkFlowRepository dao;
	@Autowired
	private WorkFlowStepService workFlowStepService;

	public WorkFlow get(String id) {
		return dao.findOne(id);
	}

	public WorkFlow save(WorkFlow workFlow){
		dao.save(workFlow);
		return workFlow;
	}

    public List<WorkFlow> getByObjectId(String objectId) {
        List<WorkFlow> workFlows = dao.findByObjectId(objectId, new Sort(Sort.Direction.DESC, "startAt"));
        for (WorkFlow workFlow : workFlows) {
            workFlow.setSteps(workFlowStepService.findByWorkFlowId(workFlow.getId()));
        }
        return workFlows;
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public WorkFlow update(WorkFlow workFlow){
        Assert.notNull(workFlow.getId(), "id can not be null");
        WorkFlow db = this.get(workFlow.getId());
        BeanUtils.copyNotNullProperties(workFlow, db);
        dao.save(db);
        return db;
	}

}