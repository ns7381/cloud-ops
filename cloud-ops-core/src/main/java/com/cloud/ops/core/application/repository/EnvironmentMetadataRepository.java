/**
 * 
 */
package com.cloud.ops.core.application.repository;

import com.cloud.ops.core.model.application.EnvironmentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface EnvironmentMetadataRepository extends JpaRepository<EnvironmentMetadata, Serializable> {

    List<EnvironmentMetadata> findByEnvId(String envId);
}
