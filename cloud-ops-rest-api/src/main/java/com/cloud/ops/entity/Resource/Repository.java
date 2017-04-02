package com.cloud.ops.entity.Resource;

import com.cloud.ops.entity.IdEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/1/13.
 */
@Entity
@Table(name="RESOURCE_REPOSITORY")
public class Repository extends IdEntity {
    private String repositoryUrl;
    private String localDir;
    private String username;
    private String password;
    private RepositoryStatus status;

    @Enumerated(EnumType.STRING)
    public RepositoryStatus getStatus() {
        return status;
    }

    public void setStatus(RepositoryStatus status) {
        this.status = status;
    }

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
