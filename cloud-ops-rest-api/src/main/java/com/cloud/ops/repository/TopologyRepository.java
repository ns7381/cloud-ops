/**
 * 
 */
package com.cloud.ops.repository;

import com.cloud.ops.entity.topology.Topology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface TopologyRepository extends JpaRepository<Topology, Serializable> {
	
}
