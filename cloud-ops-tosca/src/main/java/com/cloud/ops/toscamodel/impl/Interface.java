package com.cloud.ops.toscamodel.impl;

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
@NoArgsConstructor
public class Interface {
    String implementation;
    Map<String, Object> inputs;
    List<Map<String, Object>> dependencies;
}
