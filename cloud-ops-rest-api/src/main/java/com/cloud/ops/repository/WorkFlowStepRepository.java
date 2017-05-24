/**
 * 
 */
package com.cloud.ops.repository;

import com.cloud.ops.entity.workflow.WorkFlow;
import com.cloud.ops.entity.workflow.WorkFlowStep;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface WorkFlowStepRepository extends JpaRepository<WorkFlowStep, Serializable> {
    List<WorkFlowStep> findByWorkFlowId(String workFlowId, Sort sort);
}
