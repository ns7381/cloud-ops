/**
 * 
 */
package com.cloud.ops.core.repository;

import com.cloud.ops.entity.topology.TopologyArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TopologyArchiveRepository extends JpaRepository<TopologyArchive, Serializable> {
    List<TopologyArchive> findByTopologyId(String topologyId);
}
