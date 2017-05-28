package com.cloud.ops.esc;

import com.cloud.ops.common.exception.NotFoundException;
import com.cloud.ops.esc.docker.DockerLocationServiceImpl;
import com.cloud.ops.esc.local.LocalLocationServiceImpl;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Nathan on 2017/5/17.
 */
@Service
public class LocationServiceProvider {

    @Autowired
    private DockerLocationServiceImpl dockerLocationService;

    @Autowired
    private LocalLocationServiceImpl localLocationService;

    private LocationService match(TopologyContext topologyContext, Location location) {
        //TODO check topology and location is or not related
        if ("local".equals(location.getLocationType())) {
            return localLocationService;
        } else if ("docker".equals(location.getLocationType())) {
            return dockerLocationService;
        }
        throw new NotFoundException("not find "+ location.getLocationType() +"location");
    }

    public TopologyContext install(TopologyContext topologyContext, Location location) {
        return match(topologyContext, location).install(topologyContext, location);
    }

    public TopologyContext executeWorkFlow(TopologyContext topologyContext, Location location) {
        return match(topologyContext, location).executeWorkFlow(topologyContext, location);
    }
}
