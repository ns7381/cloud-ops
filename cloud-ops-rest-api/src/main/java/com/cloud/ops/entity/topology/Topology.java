package com.cloud.ops.entity.topology;

import com.cloud.ops.entity.BaseObject;
import com.cloud.ops.toscamodel.IToscaEnvironment;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="topology")
public class Topology extends BaseObject {
    String yamlFilePath;
    IToscaEnvironment toscaEnvironment;

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
}


