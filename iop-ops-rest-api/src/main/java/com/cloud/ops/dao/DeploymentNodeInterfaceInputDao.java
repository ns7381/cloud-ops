package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.DeploymentNodeInterfaceInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@org.springframework.stereotype.Repository
public interface DeploymentNodeInterfaceInputDao extends JpaRepository<DeploymentNodeInterfaceInput, Serializable> {
    List<DeploymentNodeInterfaceInput> findByDeploymentNodeInterfaceId(String deploymentNodeInterfaceId);
}
