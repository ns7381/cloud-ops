/**
 * 
 */
package com.cloud.ops.core.entity.Resource;

import com.cloud.ops.dao.modal.IdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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