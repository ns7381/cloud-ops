package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeProperty;
import com.cloud.ops.service.DeploymentNodePropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node/property")
public class DeploymentNodePropertyController {

    @Autowired
	private DeploymentNodePropertyService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeProperty add(@RequestBody DeploymentNodeProperty deploymentNodeProperty){
		return service.create(deploymentNodeProperty);
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
	public DeploymentNodeProperty edit(@RequestBody DeploymentNodeProperty deploymentNodeProperty){
		return service.update(deploymentNodeProperty);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNodeProperty> getByNodeId(@RequestParam String nodeId){
		return service.getByNodeId(nodeId);
	}
}
