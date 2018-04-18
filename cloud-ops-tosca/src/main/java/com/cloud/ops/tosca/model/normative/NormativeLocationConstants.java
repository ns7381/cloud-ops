package com.cloud.ops.tosca.model.normative;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NormativeLocationConstants {
    public static final String LOCATION_TYPE_CLOUD = "tosca.locations.Cloud";
    public static final String LOCATION_TYPE_OPENSTACK = "tosca.locations.Cloud.Openstack";
}