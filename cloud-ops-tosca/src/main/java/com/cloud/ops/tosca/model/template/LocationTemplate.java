package com.cloud.ops.tosca.model.template;

import com.cloud.ops.tosca.model.definition.IValue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/10.
 */
@Getter
@Setter
@Builder
public class LocationTemplate {
    String name;
    String type;
    Map<String, IValue> attributes;
}
