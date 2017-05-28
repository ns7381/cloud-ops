package com.cloud.ops.esc.local;

import com.cloud.ops.esc.Location;
import com.cloud.ops.esc.LocationService;
import com.cloud.ops.esc.wf.LocalLocationWorkFlowExecutor;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.cloud.ops.toscamodel.wf.WorkFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/26.
 */
@Service
public class LocalLocationServiceImpl implements LocationService {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public TopologyContext install(TopologyContext topologyContext, Location location) {
        //no have step that create server
        LocalLocation localLocation = (LocalLocation) location;
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
    public TopologyContext executeWorkFlow(TopologyContext topologyContext, Location location) {

        new LocalLocationWorkFlowExecutor(applicationContext, topologyContext, location).start();
        return topologyContext;
    }
}
