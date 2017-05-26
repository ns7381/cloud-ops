package com.cloud.ops.esc;

import com.cloud.ops.esc.docker.DockerLocationServiceImpl;
import com.cloud.ops.esc.local.LocalLocationServiceImpl;
import com.cloud.ops.toscamodel.impl.TopologyContext;

/**
 * Created by Nathan on 2017/5/17.
 */
public class LocationServiceMatcher {
    private LocationService locationService;

    public LocationService match(TopologyContext topologyContext, Location location) {
        if ("local".equals(location.getLocationType())) {
            this.locationService = new LocalLocationServiceImpl();
        } else if ("docker".equals(location.getLocationType())) {
            this.locationService = new DockerLocationServiceImpl();
        }
        throw new RuntimeException("not find "+ location.getLocationType() +"location");
    }
}
