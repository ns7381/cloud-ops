/**
 * 
 */
package com.cloud.ops.repository;

import com.cloud.ops.entity.application.Application;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Serializable> {
    List<Application> findByEnvironmentId(String environmentId, Sort sort);
}
