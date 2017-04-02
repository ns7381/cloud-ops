/**
 * 
 */
package com.cloud.ops.entity.Resource;

import com.cloud.ops.entity.BaseObject;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name="RESOURCE_PACKAGE")
public class ResourcePackage extends BaseObject {
	
	private String version;
	private String warPath;
	private String buildDir;
	private String repositoryId;
	private String deploymentId;
	private String branch;
	private String build;
    private ResourcePackageStatus status;

    public String getWarPath() {
        return warPath;
    }

    public void setWarPath(String warPath) {
        this.warPath = warPath;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    @Enumerated(EnumType.STRING)
    public ResourcePackageStatus getStatus() {
        return status;
    }

    public void setStatus(ResourcePackageStatus status) {
        this.status = status;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
