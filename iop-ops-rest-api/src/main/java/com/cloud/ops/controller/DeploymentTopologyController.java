package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentTopology;
import com.cloud.ops.service.DeploymentTopologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology")
public class DeploymentTopologyController {

    @Autowired
	private DeploymentTopologyService service;

	/**
	 * 新增应用程序 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentTopology addApp(@RequestBody DeploymentTopology deploymentTopology){
		return service.create(deploymentTopology);
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
	public DeploymentTopology edit(@RequestBody DeploymentTopology deploymentTopology){
		return service.update(deploymentTopology);
	}

	/**
	 * 获取所有数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentTopology> getAll(){
		return service.getAll();
	}

}
