package com.cloud.ops.tosca.model.workflow;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkFlowStep {
    private String name;
    private String host;
    private String command;
    private List<String> files = Lists.newArrayList();
}
