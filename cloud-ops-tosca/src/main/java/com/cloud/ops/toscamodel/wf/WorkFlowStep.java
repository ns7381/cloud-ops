package com.cloud.ops.toscamodel.wf;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.toscamodel.impl.Artifact;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/10.
 */
@Entity
@Table(name="work_flow_step")
public class WorkFlowStep extends BaseObject {
    private String workFlowId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endAt;
    private String host;
    private String hostIp;
    private WorkFlowStatus status;

    private Map<String, String> env;
    private String shellScript;
    private List<Artifact> artifacts;

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
    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    @Transient
    public String getShellScript() {
        return shellScript;
    }

    public void setShellScript(String shellScript) {
        this.shellScript = shellScript;
    }

    @Transient
    public List<Artifact> getArtifacts() {
        if (artifacts == null) {
            artifacts = new ArrayList<Artifact>();
        }
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    @Transient
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
