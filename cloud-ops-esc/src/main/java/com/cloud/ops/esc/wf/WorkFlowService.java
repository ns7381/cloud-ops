package com.cloud.ops.esc.wf;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.esc.wf.dao.WorkFlowRepository;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
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

	public WorkFlowEntity get(String id) {
		return dao.findOne(id);
	}

	public WorkFlowEntity save(WorkFlowEntity workFlow){
		dao.save(workFlow);
		return workFlow;
	}

    public List<WorkFlowEntity> getByObjectId(String objectId) {
        List<WorkFlowEntity> workFlows = dao.findByObjectId(objectId, new Sort(Sort.Direction.DESC, "startAt"));
        for (WorkFlowEntity workFlow : workFlows) {
            workFlow.setSteps(workFlowStepService.findByWorkFlowId(workFlow.getId()));
        }
        return workFlows;
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public WorkFlowEntity update(WorkFlowEntity workFlow){
        Assert.notNull(workFlow.getId(), "id can not be null");
        WorkFlowEntity db = this.get(workFlow.getId());
        BeanUtils.copyNotNullProperties(workFlow, db);
        dao.save(db);
        return db;
	}

}
