/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.Resource.ResourcePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Repository
public interface ResourcePackageDao extends JpaRepository<ResourcePackage, Serializable> {

    List<ResourcePackage> findByDeploymentId(String deploymentId);
}
