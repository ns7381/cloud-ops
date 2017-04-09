package com.cloud.ops.controller;

import com.cloud.ops.entity.application.Application;
import com.cloud.ops.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/applications")
public class ApplicationController {

    @Autowired
	private ApplicationService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Application create(@RequestBody Application application){
		return service.create(application);
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
     * 部署
     * @return
     */
    @RequestMapping(value = "/{id}/interface/{interfaceId}", method = RequestMethod.PUT)
    public Boolean doInterface(@PathVariable String id, @PathVariable String interfaceId, @RequestParam Map<String, Object> params){
        return service.doInterface(id, interfaceId, params);
    }

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<Application> findByEnvironmentId(@RequestParam String environmentId){
		return service.findByEnvironmentId(environmentId);
	}

}
