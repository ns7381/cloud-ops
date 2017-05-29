/**
 * 
 */
package com.cloud.ops.core.topology.repository;

import com.cloud.ops.core.model.topology.TopologyArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TopologyArchiveRepository extends JpaRepository<TopologyArchive, Serializable> {
    List<TopologyArchive> findByTopologyId(String topologyId);
}
