package com.cloud.ops.toscamodel.wf;

import com.cloud.ops.dao.modal.BaseObject;
import com.cloud.ops.toscamodel.impl.Artifact;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/10.
 */
@Entity
@Table(name="work_flow_step")
public class WorkFlowStep extends BaseObject {
    Map<String, String> env;
    String shellScript;
    List<Artifact> artifacts;
    String host;


    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public String getShellScript() {
        return shellScript;
    }

    public void setShellScript(String shellScript) {
        this.shellScript = shellScript;
    }

    public List<Artifact> getArtifacts() {
        if (artifacts == null) {
            artifacts = new ArrayList<Artifact>();
        }
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
