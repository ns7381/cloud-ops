package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNode;
import com.cloud.ops.service.DeploymentNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node")
public class DeploymentNodeController {

    @Autowired
	private DeploymentNodeService service;

	/**
	 * 新增应用程序 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNode addApp(@RequestBody DeploymentNode deploymentNode){
		return service.create(deploymentNode);
	}

    /**
     * 删除应用程序
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
	public DeploymentNode edit(@RequestBody DeploymentNode deploymentNode){
		return service.update(deploymentNode);
	}

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNode> getByTopologyId(@RequestParam String topologyId){
		return service.getByTopologyId(topologyId);
	}
}
