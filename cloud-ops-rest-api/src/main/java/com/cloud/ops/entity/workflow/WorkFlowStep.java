package com.cloud.ops.entity.workflow;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.entity.location.LocalLocation;
import com.cloud.ops.entity.topology.TopologyArchive;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Nathan on 2017/4/10.
 */
@Entity
@Table(name="work_flow_step")
public class WorkFlowStep extends BaseObject {
    private String workFlowId;
    private String hostIp;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endAt;
    private WorkFlowStatus status;


    Map<String, String> env;
    List<TopologyArchive> archives;
    LocalLocation location;

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
    public List<TopologyArchive> getArchives() {
        if (archives == null) {
            archives = new ArrayList<TopologyArchive>();
        }
        return archives;
    }

    public void setArchives(List<TopologyArchive> archives) {
        this.archives = archives;
    }

    @Transient
    public LocalLocation getLocation() {
        return location;
    }

    public void setLocation(LocalLocation location) {
        this.location = location;
    }
}
