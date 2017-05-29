/**
 * 
 */
package com.cloud.ops.core.resource.repository;

import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ResourcePackageConfigRepository extends JpaRepository<ResourcePackageConfig, Serializable> {

    ResourcePackageConfig findByApplicationId(String appId);
}
