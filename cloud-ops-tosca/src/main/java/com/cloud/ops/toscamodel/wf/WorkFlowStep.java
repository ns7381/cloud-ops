package com.cloud.ops.toscamodel.wf;

import com.cloud.ops.toscamodel.impl.Artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/10.
 */
public class WorkFlowStep {
    String name;
    Map<String, String> env;
    String shellScript;
    List<Artifact> artifacts;
    String host;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
