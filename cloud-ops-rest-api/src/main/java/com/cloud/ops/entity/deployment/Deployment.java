package com.cloud.ops.entity.deployment;

import com.cloud.ops.entity.BaseObject;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="deployment")
public class Deployment extends BaseObject {
    private String deploymentTopologyId;
    private String deploymentTopologyName;
    private String locationId;
    private String locationName;

    public String getDeploymentTopologyName() {
        return deploymentTopologyName;
    }

    public void setDeploymentTopologyName(String deploymentTopologyName) {
        this.deploymentTopologyName = deploymentTopologyName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDeploymentTopologyId() {
        return deploymentTopologyId;
    }

    public void setDeploymentTopologyId(String deploymentTopologyId) {
        this.deploymentTopologyId = deploymentTopologyId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
