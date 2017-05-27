package com.cloud.ops.entity.application;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.esc.local.Location;
import com.cloud.ops.toscamodel.IToscaEnvironment;
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
    private Location location;
    private IToscaEnvironment toscaEnvironment;
    private TopologyContext topologyContext;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Transient
    public IToscaEnvironment getToscaEnvironment() {
        return toscaEnvironment;
    }

    public void setToscaEnvironment(IToscaEnvironment toscaEnvironment) {
        this.toscaEnvironment = toscaEnvironment;
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
