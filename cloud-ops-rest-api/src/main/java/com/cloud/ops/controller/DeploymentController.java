package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.Deployment;
import com.cloud.ops.service.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/deployment")
public class DeploymentController {

    @Autowired
	private DeploymentService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Deployment create(@RequestBody Deployment deployment){
		return service.create(deployment);
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
	public List<Deployment> getAll(){
		return service.getAll();
	}

}
