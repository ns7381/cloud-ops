package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment_node_artifact")
public class DeploymentNodeArtifact extends BaseObject {
    private String path;
    private String deploymentNodeId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDeploymentNodeId() {
        return deploymentNodeId;
    }

    public void setDeploymentNodeId(String deploymentNodeId) {
        this.deploymentNodeId = deploymentNodeId;
    }

}
