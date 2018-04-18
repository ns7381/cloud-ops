package com.cloud.ops.tosca.model.definition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class PropertyDefinition implements IValue {
    private String type;

    private String description;
}
