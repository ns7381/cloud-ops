package com.cloud.ops.controller;

import com.cloud.ops.entity.Location.LocationLocal;
import com.cloud.ops.service.LocationLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/location/local")
public class LocationLocalController {

    @Autowired
	private LocationLocalService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public LocationLocal add(@RequestBody LocationLocal location){
		return service.create(location);
	}

    /**
     * 删除
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.delete(id);
        return Boolean.TRUE;
    }

	/**
	 * 编辑
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public LocationLocal edit(@RequestBody LocationLocal location){
		return service.update(location);
	}

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<LocationLocal> getByLocationId(@RequestParam String locationId){
		return service.getByLocationId(locationId);
	}

}
