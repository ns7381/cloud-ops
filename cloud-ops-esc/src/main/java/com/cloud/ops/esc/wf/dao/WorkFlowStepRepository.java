/**
 * 
 */
package com.cloud.ops.esc.wf.dao;

import com.cloud.ops.toscamodel.wf.WorkFlowStep;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface WorkFlowStepRepository extends JpaRepository<WorkFlowStep, Serializable> {
    List<WorkFlowStep> findByWorkFlowId(String workFlowId, Sort sort);
}
