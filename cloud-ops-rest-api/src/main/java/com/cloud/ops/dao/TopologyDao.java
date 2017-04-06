/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.Topology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface TopologyDao extends JpaRepository<Topology, Serializable> {
	
}
