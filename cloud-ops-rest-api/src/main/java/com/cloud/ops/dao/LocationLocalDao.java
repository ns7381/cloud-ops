/**
 * 
 */
package com.cloud.ops.dao;

import com.cloud.ops.entity.Location.LocationLocal;
import com.cloud.ops.entity.deployment.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface LocationLocalDao extends JpaRepository<LocationLocal, Serializable> {
    List<LocationLocal> findByLocationId(String locationId);

    List<LocationLocal> findByLocationIdAndType(String locationId, NodeType nodeType);
}
