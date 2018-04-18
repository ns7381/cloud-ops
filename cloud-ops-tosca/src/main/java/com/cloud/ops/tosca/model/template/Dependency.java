package com.cloud.ops.tosca.model.template;

import com.cloud.ops.tosca.model.definition.FunctionValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/17
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dependency {
    private String name;
    private FunctionValue value;
}
