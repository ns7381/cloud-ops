package com.cloud.ops.entity.application;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.entity.location.LocalLocation;
import com.cloud.ops.toscamodel.impl.TopologyContext;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
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
    String topologyName;
    String environmentId;
    TopologyContext topologyContext;

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
    public TopologyContext getTopologyContext() {
        return topologyContext;
    }

    public void setTopologyContext(TopologyContext topologyContext) {
        this.topologyContext = topologyContext;
    }

    public String getTopologyName() {
        return topologyName;
    }

    public void setTopologyName(String topologyName) {
        this.topologyName = topologyName;
    }
}
