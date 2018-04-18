package com.cloud.ops.esc.provider;

import com.cloud.ops.esc.LocationProvider;
import com.cloud.ops.esc.wf.WorkFlowExecutor;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.FunctionValue;
import com.cloud.ops.tosca.model.definition.ListPropertyValue;
import com.cloud.ops.tosca.model.definition.ScalarPropertyValue;
import com.cloud.ops.tosca.model.normative.ToscaFunctionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.cloud.ops.tosca.model.normative.NormativeNodeConstants.COMPUTE_TYPE_LOCAL;

/**
 * Created by ningsheng on 2017/5/26.
 */
@Service
public class LocalLocationProvider implements LocationProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WorkFlowExecutor workFlowExecutor;

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Topology install(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        topology.getNodeTemplatesOfType(COMPUTE_TYPE_LOCAL).forEach(node -> node.getAttributes().forEach((k, v) -> {
            if (v instanceof FunctionValue) {
                FunctionValue func = (FunctionValue) v;
                if (func.getFunction().equals(ToscaFunctionConstants.GET_INPUT)) {
                    Object input = inputs.get(func.getParameters().get(0));
                    if (input instanceof String) {
                        node.getAttributes().put(k, new ScalarPropertyValue((String) input));
                    } else if (input instanceof List) {
                        node.getAttributes().put(k, new ListPropertyValue((List) input));
                    }
                }
            }
        }));
        return topology;
    }

    @Override
    public void executeWorkFlow(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        workFlowExecutor.executeWorkFlow(topology, entity, inputs);
    }



}
