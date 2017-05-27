package com.cloud.ops.esc.local;

import com.cloud.ops.esc.LocationService;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.cloud.ops.toscamodel.wf.WorkFlow;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/26.
 */
public class LocalLocationServiceImpl implements LocationService {

    @Override
    public TopologyContext install(TopologyContext topologyContext, com.cloud.ops.esc.Location location) {
        //no have step that create server
        Location localLocation = (Location) location;
        topologyContext.getNodeTemplateMap().forEach((nodeName, node) -> {
            node.setAttributes((Map<String, Object>) localLocation.getHost().get(nodeName));
        });
        if (topologyContext.getWorkFlowMap()!=null) {
            WorkFlow install = topologyContext.getWorkFlowMap().get("install");
            if (install != null) {

            }
        }
        return topologyContext;
    }

    @Override
    public TopologyContext executeWorkFlow(TopologyContext topologyContext, com.cloud.ops.esc.Location location) {
        return topologyContext;
    }
}
