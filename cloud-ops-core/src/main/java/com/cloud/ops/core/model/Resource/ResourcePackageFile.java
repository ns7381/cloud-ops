/**
 * 
 */
package com.cloud.ops.core.model.Resource;

import com.cloud.ops.dao.modal.IdEntity;

import javax.persistence.*;

@Entity
@Table(name="RESOURCE_PACKAGE_FILE")
public class ResourcePackageFile extends IdEntity {
	
    private String applicationId;

    private String name;
    private ResourcePackageFileType type;
    private String path;
    private String seed;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    public ResourcePackageFileType getType() {
        return type;
    }

    public void setType(ResourcePackageFileType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }
}
