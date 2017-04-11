/**
 * 
 */
package com.cloud.ops.entity.Resource;

import com.cloud.ops.entity.BaseObject;
import com.cloud.ops.entity.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="RESOURCE_PACKAGE_CONFIG")
@Getter
@Setter
public class ResourcePackageConfig extends IdEntity {
	
    private String applicationId;

    private String gitUrl;
    private String gitUsername;
    private String gitPassword;
    private String branch;

    private String build;
    private String buildDir;
}
