/**
 * 
 */
package com.cloud.ops.core.repository;

import com.cloud.ops.entity.application.ApplicationEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface ApplicationEnvironmentRepository extends JpaRepository<ApplicationEnvironment, Serializable> {
	
}
