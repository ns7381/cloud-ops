package com.cloud.ops.tosca.model.definition;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScalarPropertyValue extends PropertyValue<String> {

    public ScalarPropertyValue(String value) {
        super(value);
    }
}