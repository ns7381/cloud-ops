package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment_node_interface")
public class DeploymentNodeInterface extends BaseObject {
    private String implementation;
    private String deploymentNodeId;
    private List<DeploymentNodeInterfaceInput> interfaceInputs;

    @Transient
    public List<DeploymentNodeInterfaceInput> getInterfaceInputs() {
        return interfaceInputs;
    }

    public void setInterfaceInputs(List<DeploymentNodeInterfaceInput> interfaceInputs) {
        this.interfaceInputs = interfaceInputs;
    }

    public String getDeploymentNodeId() {
        return deploymentNodeId;
    }

    public void setDeploymentNodeId(String deploymentNodeId) {
        this.deploymentNodeId = deploymentNodeId;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }
}
