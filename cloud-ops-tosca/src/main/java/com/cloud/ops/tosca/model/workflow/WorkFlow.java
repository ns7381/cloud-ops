package com.cloud.ops.tosca.model.workflow;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkFlow {
    private String name;
    private List<WorkFlowStep> steps;
}
