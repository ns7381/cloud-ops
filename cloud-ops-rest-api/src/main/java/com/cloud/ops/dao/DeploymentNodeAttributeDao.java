/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.DeploymentNodeArtifact;
import com.cloud.ops.entity.deployment.DeploymentNodeAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface DeploymentNodeAttributeDao extends JpaRepository<DeploymentNodeAttribute, Serializable> {
    List<DeploymentNodeAttribute> findByDeploymentNodeId(String name);
}
