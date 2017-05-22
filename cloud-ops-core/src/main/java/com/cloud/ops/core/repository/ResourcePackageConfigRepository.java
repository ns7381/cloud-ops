/**
 * 
 */
package com.cloud.ops.core.repository;

import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ResourcePackageConfigRepository extends JpaRepository<ResourcePackageConfig, Serializable> {

    ResourcePackageConfig findByApplicationId(String appId);
}
