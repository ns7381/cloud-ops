package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeAttribute;
import com.cloud.ops.service.DeploymentNodeAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node/attribute")
public class DeploymentNodeAttributeController {

    @Autowired
	private DeploymentNodeAttributeService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeAttribute add(@RequestBody DeploymentNodeAttribute deploymentNodeAttribute){
		return service.create(deploymentNodeAttribute);
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
	public DeploymentNodeAttribute edit(@RequestBody DeploymentNodeAttribute deploymentNodeAttribute){
		return service.update(deploymentNodeAttribute);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNodeAttribute> getByNodeId(@RequestParam String nodeId){
		return service.getByNodeId(nodeId);
	}
}
