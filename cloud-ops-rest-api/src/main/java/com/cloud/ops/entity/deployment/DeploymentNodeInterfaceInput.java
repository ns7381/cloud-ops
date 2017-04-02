package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment_node_interface_input")
public class DeploymentNodeInterfaceInput extends IdEntity {
    private String name;
    private String value;
    private DeploymentNodeInterfaceInputType type;
    private String deploymentNodeInterfaceId;

    @Enumerated(EnumType.STRING)
    public DeploymentNodeInterfaceInputType getType() {
        return type;
    }

    public void setType(DeploymentNodeInterfaceInputType type) {
        this.type = type;
    }

    public String getDeploymentNodeInterfaceId() {
        return deploymentNodeInterfaceId;
    }

    public void setDeploymentNodeInterfaceId(String deploymentNodeInterfaceId) {
        this.deploymentNodeInterfaceId = deploymentNodeInterfaceId;
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
