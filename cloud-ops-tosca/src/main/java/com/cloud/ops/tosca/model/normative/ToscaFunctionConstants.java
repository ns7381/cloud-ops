package com.cloud.ops.tosca.model.normative;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToscaFunctionConstants {

    /* possible functions */
    public static final String GET_ARTIFACT = "get_artifact";
    public static final String GET_ATTRIBUTE = "get_attribute";
    public static final String GET_INPUT = "get_input";
    public static final String DO_INTERFACE = "do_interface";

    /* reserved keywords */
    public static final String SELF = "SELF";
    public static final String TARGET = "TARGET";
    public static final String SOURCE = "SOURCE";
    public static final String HOST = "HOST";
}
