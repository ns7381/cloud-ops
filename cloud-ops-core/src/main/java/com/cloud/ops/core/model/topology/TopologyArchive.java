package com.cloud.ops.core.model.topology;

import com.cloud.ops.dao.modal.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology_archive")
public class TopologyArchive extends BaseObject {
    private String topologyId;
    private String filePath;
    private TopologyArchiveType type;
    List<String> fileContents;

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

    @Transient
    public List<String> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<String> fileContents) {
        this.fileContents = fileContents;
    }
}
