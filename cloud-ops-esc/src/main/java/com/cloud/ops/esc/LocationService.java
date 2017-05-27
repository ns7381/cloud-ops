package com.cloud.ops.esc;

import com.cloud.ops.toscamodel.impl.TopologyContext;

/**
 * Created by ningsheng on 2017/5/26.
 */
public interface LocationService {

    TopologyContext install(TopologyContext topologyContext, Location location);

    TopologyContext executeWorkFlow(TopologyContext topologyContext, Location location);
}
