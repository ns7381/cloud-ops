package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.DeploymentNodeRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@org.springframework.stereotype.Repository
public interface DeploymentNodeRequirementDao extends JpaRepository<DeploymentNodeRequirement, Serializable> {
    List<DeploymentNodeRequirement> findByDeploymentNodeId(String nodeId);

    DeploymentNodeRequirement findByDeploymentNodeIdAndName(String nodeId, String name);
}
