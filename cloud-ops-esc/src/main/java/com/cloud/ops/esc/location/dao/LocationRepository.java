/**
 *
 */
package com.cloud.ops.esc.location.dao;

import com.cloud.ops.esc.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Serializable> {
    List<Location> findByType(String type);
}
