package com.cloud.ops.core.entity.application;

/**
 * Created by Nathan on 2017/4/14.
 */
public enum DeploymentType {
    PATCH_DEPLOY("patch_deploy"),
    WAR_DEPLOY("war_deploy");

    private String type;
    DeploymentType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
