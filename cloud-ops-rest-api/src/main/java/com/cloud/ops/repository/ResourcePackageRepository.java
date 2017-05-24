/**
 * 
 */
package com.cloud.ops.repository;

import com.cloud.ops.entity.Resource.ResourcePackage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface ResourcePackageRepository extends JpaRepository<ResourcePackage, Serializable> {

    List<ResourcePackage> findByApplicationId(String appId, Sort sort);
}
