package com.cloud.ops.tosca.model.template;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Nathan on 2017/4/5.
 */
@Getter
@Setter
@NoArgsConstructor
public class Artifact {
    String file;
    String type;
}
