package com.cloud.ops.controller;

import com.cloud.ops.entity.Location.Location;
import com.cloud.ops.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/location")
public class LocationController {

    @Autowired
    private LocationService service;


    @RequestMapping(method = RequestMethod.POST)
    public Location create(@RequestBody Location location) {
        return service.add(location);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    /**
     * 编辑
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public Location edit(@RequestBody Location location) {
        return service.edit(location);
    }

    /**
     * 获取所有数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Location> getAll() {
        return service.getAll();
    }

}
