/**
 * 
 */
package com.cloud.ops.entity.Resource;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="RESOURCE_PACKAGE")
public class ResourcePackage extends BaseObject {
	
	private String version;
    private String applicationId;
    private ResourcePackageConfig config;
    private String warPath;
    private ResourcePackageStatus status;
    private ResourcePackageType type;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Transient
    public ResourcePackageConfig getConfig() {
        return config;
    }

    public void setConfig(ResourcePackageConfig config) {
        this.config = config;
    }

    public String getWarPath() {
        return warPath;
    }

    public void setWarPath(String warPath) {
        this.warPath = warPath;
    }

    @Enumerated(EnumType.STRING)
    public ResourcePackageStatus getStatus() {
        return status;
    }

    public void setStatus(ResourcePackageStatus status) {
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    public ResourcePackageType getType() {
        return type;
    }

    public void setType(ResourcePackageType type) {
        this.type = type;
    }
}
