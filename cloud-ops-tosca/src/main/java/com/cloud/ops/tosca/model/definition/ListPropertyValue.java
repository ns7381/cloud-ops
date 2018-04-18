package com.cloud.ops.tosca.model.definition;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ListPropertyValue extends PropertyValue<List<String>> {
    public ListPropertyValue() {
        super(new ArrayList<>());
    }

    public ListPropertyValue(List<String> value) {
        super(value);
    }
}
