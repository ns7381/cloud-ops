package com.cloud.ops.tosca.model.template;

import com.cloud.ops.tosca.model.definition.FunctionValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 2017/4/6.
 */
@Getter
@Setter
public class Interface {
    String implementation;
    Map<String, FunctionValue> inputs;
    List<Dependency> dependencies;

    public Interface() {
        this.inputs = Maps.newHashMap();
        this.dependencies = Lists.newArrayList();
    }
}
