package com.cloud.ops.entity.topology;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.entity.application.DeploymentNode;
import com.cloud.ops.toscamodel.IToscaEnvironment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology")
public class Topology extends BaseObject {
    String yamlFilePath;
    IToscaEnvironment toscaEnvironment;
    List<DeploymentNode> nodes;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public IToscaEnvironment getToscaEnvironment() {
        return toscaEnvironment;
    }

    public void setToscaEnvironment(IToscaEnvironment toscaEnvironment) {
        this.toscaEnvironment = toscaEnvironment;
    }

    @Transient
    public List<DeploymentNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DeploymentNode> nodes) {
        this.nodes = nodes;
    }
}


