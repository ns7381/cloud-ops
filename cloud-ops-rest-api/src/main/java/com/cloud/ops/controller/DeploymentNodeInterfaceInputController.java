package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeInterfaceInput;
import com.cloud.ops.service.DeploymentNodeInterfaceInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node/interface/input")
public class DeploymentNodeInterfaceInputController {

    @Autowired
	private DeploymentNodeInterfaceInputService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeInterfaceInput add(@RequestBody DeploymentNodeInterfaceInput deploymentNodeInterfaceInput){
		return service.create(deploymentNodeInterfaceInput);
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
	public DeploymentNodeInterfaceInput edit(@RequestBody DeploymentNodeInterfaceInput deploymentNodeInterfaceInput){
		return service.update(deploymentNodeInterfaceInput);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNodeInterfaceInput> getByInterfaceId(@RequestParam String interfaceId){
		return service.getByInterfaceId(interfaceId);
	}
}
