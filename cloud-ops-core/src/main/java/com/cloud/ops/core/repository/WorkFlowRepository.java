/**
 * 
 */
package com.cloud.ops.core.repository;

import com.cloud.ops.entity.workflow.WorkFlow;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface WorkFlowRepository extends JpaRepository<WorkFlow, Serializable> {
    List<WorkFlow> findByObjectId(String objectId, Sort sort);
}
