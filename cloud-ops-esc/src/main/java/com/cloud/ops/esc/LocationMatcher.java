package com.cloud.ops.esc;

import com.cloud.ops.common.exception.OpsException;
import com.cloud.ops.esc.location.LocationService;
import com.cloud.ops.esc.provider.LocalLocationProvider;
import com.cloud.ops.esc.provider.jclouds.DockerLocationProvider;
import com.cloud.ops.esc.provider.jclouds.OpenstackLocationProvider;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.tosca.Tosca;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.ScalarPropertyValue;
import com.cloud.ops.tosca.model.template.LocationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.cloud.ops.tosca.model.normative.NormativeNodeConstants.*;
import static com.cloud.ops.tosca.model.normative.NormativeLocationConstants.LOCATION_TYPE_OPENSTACK;

/**
 * Created by Nathan on 2017/5/17.
 */
@Service
public class LocationMatcher {
    @Autowired
    private DockerLocationProvider docker;
    @Autowired
    private LocalLocationProvider local;
    @Autowired
    private OpenstackLocationProvider openstack;
    @Autowired
    private LocationService locationService;

    private LocationProvider match(Topology topology) {
        switch (topology.getComputeType(COMPUTE_TYPE)) {
            case COMPUTE_TYPE_LOCAL:
                return local;
            case COMPUTE_TYPE_OPENSTACK:
                String yamlFilePath = locationService.findByType(LOCATION_TYPE_OPENSTACK).get(0).getYamlFilePath();
                Map<String, LocationTemplate> locationTemplates = Tosca.getLocationTemplates(yamlFilePath);
                Optional<Map.Entry<String, LocationTemplate>> first = locationTemplates.entrySet().stream().findFirst();
                if (first.isPresent()) {
                    LocationTemplate loc = first.get().getValue();
                    openstack.setEndpoint(((ScalarPropertyValue) loc.getAttributes().get("endpoint")).getValue());
                    openstack.setIdentity(((ScalarPropertyValue) loc.getAttributes().get("identity")).getValue());
                    openstack.setCredential(((ScalarPropertyValue) loc.getAttributes().get("credential")).getValue());
                } else {
                    throw new OpsException("not find location");
                }
                return openstack;
            case COMPUTE_TYPE_DOCKER:
                return docker;
            default:
                return local;
        }
    }

    public Topology install(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        return match(topology).install(topology, entity, inputs);
    }

    public void executeWorkFlow(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        match(topology).executeWorkFlow(topology, entity, inputs);
    }
}
