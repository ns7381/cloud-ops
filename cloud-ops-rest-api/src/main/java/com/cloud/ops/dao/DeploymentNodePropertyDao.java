/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.DeploymentNodeProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface DeploymentNodePropertyDao extends JpaRepository<DeploymentNodeProperty, Serializable> {
    List<DeploymentNodeProperty> findByDeploymentNodeId(String nodeId);
}
