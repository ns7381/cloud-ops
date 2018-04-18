/**
 * 
 */
package com.cloud.ops.core.application.repository;

import com.cloud.ops.core.model.application.Application;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Serializable> {
    List<Application> findByEnvironmentId(String environmentId, Sort sort);
}
