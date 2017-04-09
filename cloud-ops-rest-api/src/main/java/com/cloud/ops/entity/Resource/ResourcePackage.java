/**
 * 
 */
package com.cloud.ops.entity.Resource;

import com.cloud.ops.entity.BaseObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="RESOURCE_PACKAGE")
@Getter
@Setter
public class ResourcePackage extends BaseObject {
	
	private String version;
    private String applicationId;

    private String gitUrl;
    private String gitUsername;
    private String gitPassword;

    private String branch;
    private String build;
    private String buildDir;
    private String warPath;
    @Enumerated(EnumType.STRING)
    private ResourcePackageStatus status;
}
