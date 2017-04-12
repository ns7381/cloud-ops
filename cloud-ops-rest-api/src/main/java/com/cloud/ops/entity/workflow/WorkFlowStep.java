package com.cloud.ops.entity.workflow;

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
public class WorkFlowStep extends IdEntity {
    String name;
    String message;
    private String workFlowId;
    private Date startAt;
    private Date endAt;
    private WorkFlowStatus status;
    String scriptFilePath;
    Map<String, Object> env;
    List<Artifact> artifacts;
    List<Map<String, String>> locations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void appendMessage(String message) {
        this.message += message;
    }

    public String getWorkFlowId() {
        return workFlowId;
    }

    public void setWorkFlowId(String workFlowId) {
        this.workFlowId = workFlowId;
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
