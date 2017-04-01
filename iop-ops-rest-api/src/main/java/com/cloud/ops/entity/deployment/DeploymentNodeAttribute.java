package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment_node_attribute")
public class DeploymentNodeAttribute extends BaseObject {
    private String deploymentNodeId;
    private String value;

    public String getDeploymentNodeId() {
        return deploymentNodeId;
    }

    public void setDeploymentNodeId(String deploymentNodeId) {
        this.deploymentNodeId = deploymentNodeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
