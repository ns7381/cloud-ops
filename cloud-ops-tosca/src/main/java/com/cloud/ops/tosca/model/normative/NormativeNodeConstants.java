package com.cloud.ops.tosca.model.normative;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NormativeNodeConstants {
    public static final String COMPUTE_TYPE = "tosca.nodes.Compute";
    public static final String COMPUTE_TYPE_LOCAL = "tosca.nodes.Compute.Local";
    public static final String COMPUTE_TYPE_CLOUD = "tosca.nodes.Compute.Cloud";
    public static final String COMPUTE_TYPE_DOCKER = "tosca.nodes.Compute.Cloud.Docker";
    public static final String COMPUTE_TYPE_OPENSTACK = "tosca.nodes.Compute.Cloud.Openstack";

    public static final String DEPLOY_TYPE = "tosca.nodes.deploy";
}