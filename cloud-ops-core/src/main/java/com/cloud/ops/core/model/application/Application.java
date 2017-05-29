package com.cloud.ops.core.model.application;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.esc.local.LocalLocation;
import com.cloud.ops.toscamodel.impl.TopologyContext;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="application")
public class Application extends BaseObject {
    private String yamlFilePath;
    private String topologyId;
    private String topologyName;
    private String environmentId;
    private LocalLocation location;
    private TopologyContext topologyContext;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public LocalLocation getLocation() {
        return location;
    }

    public void setLocation(LocalLocation location) {
        this.location = location;
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
