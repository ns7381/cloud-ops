package com.cloud.ops.entity.workflow;

import com.cloud.ops.entity.BaseObject;
import com.cloud.ops.entity.IdEntity;
import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.IValue;
import com.cloud.ops.toscamodel.basictypes.IValueList;
import com.cloud.ops.toscamodel.basictypes.IValueString;
import com.cloud.ops.toscamodel.impl.Artifact;
import com.cloud.ops.toscamodel.impl.Interface;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Nathan on 2017/4/10.
 */
@Entity
@Table(name="work_flow")
public class WorkFlowStep extends BaseObject {
    private String workFlowId;
    private String hostIp;
    private String user;
    private String password;
    private Date startAt;
    private Date endAt;
    private WorkFlowStatus status;
    String scriptFilePath;
    Map<String, Object> env;
    List<Artifact> artifacts;
    List<Map<String, String>> locations;

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    @Transient
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Transient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    @Enumerated(EnumType.STRING)
    public WorkFlowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkFlowStatus status) {
        this.status = status;
    }

    @Transient
    public String getScriptFilePath() {
        return scriptFilePath;
    }

    public void setScriptFilePath(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath;
    }

    @Transient
    public Map<String, Object> getEnv() {
        return env;
    }

    public void setEnv(Map<String, Object> env) {
        this.env = env;
    }

    @Transient
    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @Transient
    public List<Map<String, String>> getLocations() {
        return locations;
    }

    public void setLocations(List<Map<String, String>> locations) {
        this.locations = locations;
    }
}
