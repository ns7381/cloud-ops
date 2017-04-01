package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment_node_requirement")
public class DeploymentNodeRequirement extends IdEntity {
    private String name;
    private String value;
    private String deploymentNodeId;

    public String getDeploymentNodeId() {
        return deploymentNodeId;
    }

    public void setDeploymentNodeId(String deploymentNodeId) {
        this.deploymentNodeId = deploymentNodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
