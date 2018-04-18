package com.cloud.ops.tosca.model;

import com.cloud.ops.tosca.Tosca;
import com.cloud.ops.tosca.model.definition.IValue;
import com.cloud.ops.tosca.model.definition.ListPropertyValue;
import com.cloud.ops.tosca.model.definition.PropertyDefinition;
import com.cloud.ops.tosca.model.definition.ScalarPropertyValue;
import com.cloud.ops.tosca.model.template.NodeTemplate;
import com.cloud.ops.tosca.model.type.NodeType;
import com.cloud.ops.tosca.model.workflow.WorkFlow;
import com.cloud.ops.tosca.model.workflow.WorkFlowBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Nathan on 2017/5/23.
 */
@Getter
@Setter
public class Topology {
    private Map<String, NodeTemplate> nodeTemplates;
    private Map<String, PropertyDefinition> inputs;

    public Topology() {
        this.nodeTemplates = Maps.newHashMap();
        this.inputs = Maps.newHashMap();
    }

    public WorkFlow getWorkFlow(String nodeName, String interfaceName) {
        return WorkFlowBuilder.buildWorkFlow(this, nodeName, interfaceName);
    }

    public List<NodeTemplate> getNodeTemplatesOfType(String type) {
        List<NodeTemplate> result = Lists.newArrayList();
        List<String> nodeTypes = Lists.newArrayList();
        nodeTypes.add(type);
        Map<String, NodeType> allNodeTypes = Tosca.getNodeTypes();
        nodeTypes.addAll(findChildType(type, allNodeTypes));
        this.getNodeTemplates().forEach((k, v) -> {
            if (nodeTypes.contains(v.getType())) {
                result.add(v);
            }
        });
        return result;
    }

    private List<String> findChildType(String type, Map<String, NodeType> allNodeTypes) {
        List<String> nodeTypes = Lists.newArrayList();
        allNodeTypes.forEach((k, v) -> {
            if (v.getDerivedFrom().equals(type)) {
                nodeTypes.add(k);
                nodeTypes.addAll(findChildType(k, allNodeTypes));
            }
        });
        return nodeTypes;
    }

    public String getComputeType(String type) {
        List<NodeTemplate> templates = getNodeTemplatesOfType(type);
        if (!templates.isEmpty()) {
            return templates.get(0).getType();
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateAttributes(String nodeName, Map<String, Object> attributes) {
        Map<String, IValue> valueMap = nodeTemplates.get(nodeName).getAttributes();
        valueMap.forEach((k, v) -> {
            if (attributes.get(k) != null) {
                if (v instanceof ScalarPropertyValue) {
                    valueMap.put(k, new ScalarPropertyValue((String) attributes.get(k)));
                } else if (v instanceof ListPropertyValue) {
                    valueMap.put(k, new ListPropertyValue((List<String>) attributes.get(k)));
                }
            }
        });
    }
}
