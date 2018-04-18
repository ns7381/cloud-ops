package com.cloud.ops.esc.provider.jclouds;

import com.cloud.ops.esc.LocationProvider;
import com.cloud.ops.esc.provider.JcloudsLocation;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.tosca.model.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DockerLocationProvider extends JcloudsLocation implements LocationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcloudsLocation.class);


    @Override
    public Topology install(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        return null;
    }

    @Override
    public void executeWorkFlow(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
    }
}
