/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface DeploymentDao extends JpaRepository<Deployment, Serializable> {
    @Query(value = "SELECT count(u.id) FROM deployment u WHERE u.is_deleted='1'", nativeQuery = true)
    long countDeletedEntries();
}
