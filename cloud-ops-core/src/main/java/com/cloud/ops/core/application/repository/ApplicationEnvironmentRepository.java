/**
 * 
 */
package com.cloud.ops.core.application.repository;

import com.cloud.ops.core.model.application.ApplicationEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ApplicationEnvironmentRepository extends JpaRepository<ApplicationEnvironment, Serializable> {
	
}
