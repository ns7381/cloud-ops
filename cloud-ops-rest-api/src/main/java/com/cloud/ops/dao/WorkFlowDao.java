/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface WorkFlowDao extends JpaRepository<WorkFlow, Serializable> {
    List<WorkFlow> findByObjectId(String objectId);
}