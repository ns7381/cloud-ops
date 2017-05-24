package com.cloud.ops.toscamodel.impl;

import com.cloud.ops.toscamodel.wf.WorkFlow;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by Nathan on 2017/5/23.
 */
@Getter
@Setter
@Builder
public class TopologyContext {
    private Map<String, NodeTemplateDto> nodeTemplateMap;
    private Map<String, WorkFlow> workFlowMap;
}
