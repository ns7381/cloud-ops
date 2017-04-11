/**
 * 
 */
package com.cloud.ops.repository;

import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface ResourcePackageConfigRepository extends JpaRepository<ResourcePackageConfig, Serializable> {

    ResourcePackageConfig findByApplicationId(String appId);
}
