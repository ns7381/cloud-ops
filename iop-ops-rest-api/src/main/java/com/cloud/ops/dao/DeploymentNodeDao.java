/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.DeploymentNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface DeploymentNodeDao extends JpaRepository<DeploymentNode, Serializable> {
    List<DeploymentNode> findByDeploymentTopologyId(String topologyId);
}
