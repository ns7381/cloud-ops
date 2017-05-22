package com.cloud.ops.core.entity.application;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.toscamodel.IToscaEnvironment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="application")
public class Application extends BaseObject {
    String yamlFilePath;
    Map<String, LocalLocation> locations;
    String topologyId;
    String topologyName;//TODO KVMapping
    String environmentId;
    IToscaEnvironment toscaEnvironment;
    List<DeploymentNode> nodes;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public Map<String, LocalLocation> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, LocalLocation> locations) {
        this.locations = locations;
    }

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    @Transient
    public IToscaEnvironment getToscaEnvironment() {
        return toscaEnvironment;
    }

    public void setToscaEnvironment(IToscaEnvironment toscaEnvironment) {
        this.toscaEnvironment = toscaEnvironment;
    }

    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }

    @Transient
    public List<DeploymentNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<DeploymentNode> nodes) {
        this.nodes = nodes;
    }
}
