package com.cloud.ops.core.model.topology;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.tosca.model.Topology;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology")
public class TopologyEntity extends BaseObject {
    private String yamlFilePath;
    private Topology topology;
    private List<String> fileContents;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    @Transient
    public List<String> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<String> fileContents) {
        this.fileContents = fileContents;
    }
}


