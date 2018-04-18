/**
 * 
 */
package com.cloud.ops.esc.location;

import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.esc.location.dao.LocationRepository;
import com.cloud.ops.esc.location.model.Location;
import com.cloud.ops.dao.modal.SortConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Transactional
public class LocationService {
	
	@Autowired
	private LocationRepository dao;
	
	public Location get(String id){
		return dao.findOne(id);
	}

    public List<Location> findByType(String name) {
		return dao.findByType(name);
	}

    public List<Location> getAll() {
		return dao.findAll(SortConstant.CREATED_AT);
	}

    public Location create(Location location) {
        dao.save(location);
        return location;
    }

    public Location update(Location location) {
        Assert.notNull(location.getId(), "id can not be null");
        Location db = this.get(location.getId());
        BeanUtils.copyNotNullProperties(location, db);
        dao.save(db);
        return db;
    }

    public void delete(String id) {
        Assert.hasLength(id, "id can not be null");
        dao.delete(id);
    }
}
