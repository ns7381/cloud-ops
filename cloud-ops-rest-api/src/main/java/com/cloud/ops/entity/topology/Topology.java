package com.cloud.ops.entity.topology;

import com.cloud.ops.entity.BaseObject;
import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.IToscaEnvironment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology")
public class Topology extends BaseObject {
    String yamlFilePath;
    IToscaEnvironment toscaEnvironment;
    List<INodeTemplate> computeNodes;

    public String getYamlFilePath() {
        return yamlFilePath;
    }

    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Transient
    public IToscaEnvironment getToscaEnvironment() {
        return toscaEnvironment;
    }

    public void setToscaEnvironment(IToscaEnvironment toscaEnvironment) {
        this.toscaEnvironment = toscaEnvironment;
    }

    @Transient
    public List<INodeTemplate> getComputeNodes() {
        return computeNodes;
    }

    public void setComputeNodes(List<INodeTemplate> computeNodes) {
        this.computeNodes = computeNodes;
    }
}


