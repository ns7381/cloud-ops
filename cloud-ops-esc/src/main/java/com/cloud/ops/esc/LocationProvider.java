package com.cloud.ops.esc;

import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.tosca.model.Topology;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/26.
 */
public interface LocationProvider {

    Topology install(Topology topologyContext, WorkFlowEntity entity, Map<String, Object> inputs);

    void executeWorkFlow(Topology topologyContext, WorkFlowEntity entity, Map<String, Object> inputs);

}
