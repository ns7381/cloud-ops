/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.deployment.TopologyArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TopologyArchiveDao extends JpaRepository<TopologyArchive, Serializable> {
    List<TopologyArchive> findByTopologyId(String topologyId);
}
