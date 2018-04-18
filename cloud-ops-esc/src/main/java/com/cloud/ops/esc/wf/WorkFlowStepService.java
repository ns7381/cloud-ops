package com.cloud.ops.esc.wf;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.esc.wf.dao.WorkFlowStepRepository;
import com.cloud.ops.esc.wf.model.WorkFlowStepEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class WorkFlowStepService {

    @Autowired
    private WorkFlowStepRepository dao;

    public WorkFlowStepEntity get(String id) {
        return dao.findOne(id);
    }

    public WorkFlowStepEntity save(WorkFlowStepEntity entity) {
        dao.save(entity);
        return entity;
    }

    public List<WorkFlowStepEntity> findByWorkFlowId(String workFlowId) {
        return dao.findByWorkFlowId(workFlowId, new Sort(Sort.Direction.ASC, "index"));
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public WorkFlowStepEntity update(WorkFlowStepEntity entity) {
        Assert.notNull(entity.getId(), "id can not be null");
        WorkFlowStepEntity db = this.get(entity.getId());
        BeanUtils.copyNotNullProperties(entity, db);
        dao.save(db);
        return db;
    }

}
