/**
 * 
 */
package com.cloud.ops.core.topology.repository;

import com.cloud.ops.core.model.topology.Topology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface TopologyRepository extends JpaRepository<Topology, Serializable> {
	
}
