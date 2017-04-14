package com.cloud.ops.entity.topology;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology_archive")
public class TopologyArchive extends BaseObject {
    private String topologyId;
    private String filePath;
    private TopologyArchiveType type;

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Transient
    public TopologyArchiveType getType() {
        return type;
    }

    public void setType(TopologyArchiveType type) {
        this.type = type;
    }
}
