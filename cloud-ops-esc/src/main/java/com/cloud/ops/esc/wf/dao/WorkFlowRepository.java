/**
 * 
 */
package com.cloud.ops.esc.wf.dao;

import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface WorkFlowRepository extends JpaRepository<WorkFlowEntity, Serializable> {
    List<WorkFlowEntity> findByObjectId(String objectId, Sort sort);
}
