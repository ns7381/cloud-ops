package com.cloud.ops.toscamodel.impl;

import com.cloud.ops.toscamodel.INodeTemplate;
import com.cloud.ops.toscamodel.IValue;
import com.cloud.ops.toscamodel.basictypes.IValueList;
import com.cloud.ops.toscamodel.basictypes.IValueString;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/10.
 */
@Getter
@Setter
@Builder
public class NodeTemplateDto {
    String name;
    String type;
    Map<String, Object> attributes;
    List<Map<String, Object>> requirements;
    Map<String, Artifact> artifacts;
    Map<String, Interface> interfaces;

    public static NodeTemplateDto convert(INodeTemplate nodeTemplate) {
        Map<String, Object> attr = new HashMap<>();
        List<String> strList = new ArrayList<>();
        for (Map.Entry<String, IValue> entry : nodeTemplate.declaredAttributes().entrySet()) {
            IValue value = entry.getValue();
            if (value instanceof IValueString) {
                attr.put(entry.getKey(), ((IValueString) value).get());
            } else if (value instanceof IValueList) {
                List<IValue> list = ((IValueList) value).get();
                for (IValue iValue : list) {
                    strList.add(((IValueString) iValue).get());
                }
                attr.put(entry.getKey(), strList);
            }
        }
        return builder().name(nodeTemplate.toString()).type(nodeTemplate.baseType().toString())
                .attributes(attr).interfaces(nodeTemplate.declaredInterfaces())
                .artifacts(nodeTemplate.declaredArtifacts()).requirements(nodeTemplate.declaredRequirements())
                .build();
    }
}
