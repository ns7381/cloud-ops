package com.cloud.ops.tosca.model.type;

import com.cloud.ops.tosca.model.definition.IValue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/19
 */
@Getter
@Setter
@Builder
public class LocationType {
    private String derivedFrom;
    private Map<String, IValue> attributes;
}
