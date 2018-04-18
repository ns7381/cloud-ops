package com.cloud.ops.tosca.model.definition;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/16
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class PropertyValue<T> implements IValue {
    protected T value;
}
