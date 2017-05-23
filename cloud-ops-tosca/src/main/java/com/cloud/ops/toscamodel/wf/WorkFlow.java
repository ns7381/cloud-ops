package com.cloud.ops.toscamodel.wf;

import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Administrator on 2017/2/9.
 */
@NoArgsConstructor
public class WorkFlow {
    public static final String PATCH_DEPLOY_WF = "patch_deploy";
    public static final String ARTIFACT_PATH = "/opt/iop-ops/artifact";
    private String name;
    private List<WorkFlowStep> steps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkFlowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkFlowStep> steps) {
        this.steps = steps;
    }
}
