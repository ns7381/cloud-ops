package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeRequirement;
import com.cloud.ops.service.DeploymentNodeRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node/requirement")
public class DeploymentNodeRequirementController {

    @Autowired
	private DeploymentNodeRequirementService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeRequirement add(@RequestBody DeploymentNodeRequirement deploymentNodeRequirement){
		return service.create(deploymentNodeRequirement);
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
	public DeploymentNodeRequirement edit(@RequestBody DeploymentNodeRequirement deploymentNodeRequirement){
		return service.update(deploymentNodeRequirement);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNodeRequirement> getByNodeId(@RequestParam String nodeId){
		return service.getByNodeId(nodeId);
	}
}
