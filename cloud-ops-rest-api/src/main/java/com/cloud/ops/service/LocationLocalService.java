package com.cloud.ops.service;

import com.cloud.ops.dao.LocationLocalDao;
import com.cloud.ops.entity.Location.LocationLocal;
import com.cloud.ops.entity.deployment.NodeType;

import com.cloud.ops.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LocationLocalService {
	
	@Autowired
	private LocationLocalDao dao;

	public LocationLocal get(String id) {
		return dao.findOne(id);
	}

	public LocationLocal create(LocationLocal locationLocal){
		dao.save(locationLocal);
		return locationLocal;
	}

    public List<LocationLocal> getByLocationId(String locationId) {
        return dao.findByLocationId(locationId);
    }

    public List<LocationLocal> getByLocationIdAndType(String locationId, NodeType nodeType) {
        return dao.findByLocationIdAndType(locationId, nodeType);
    }

    public void delete(String id) {
        dao.delete(id);
    }

	public LocationLocal update(LocationLocal locationLocal){
        Assert.notNull(locationLocal.getId());
        LocationLocal db = this.get(locationLocal.getId());
        BeanUtils.copyNotNullProperties(locationLocal, db);
        dao.save(db);
        return db;
	}

}
