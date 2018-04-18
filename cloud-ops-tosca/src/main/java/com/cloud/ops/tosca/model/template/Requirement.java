package com.cloud.ops.tosca.model.template;

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
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {
    private String type;
    private String value;
}
