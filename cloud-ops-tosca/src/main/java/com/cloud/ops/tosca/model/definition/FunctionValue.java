package com.cloud.ops.tosca.model.definition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/16
 */
@Getter
@Setter
@NoArgsConstructor
public class FunctionValue implements IValue {
    private String function;
    private List<String> parameters;
}
