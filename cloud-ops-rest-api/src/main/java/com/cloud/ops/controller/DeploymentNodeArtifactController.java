package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeArtifact;
import com.cloud.ops.service.DeploymentNodeArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/topology/node/artifact")
public class DeploymentNodeArtifactController {

    @Autowired
	private DeploymentNodeArtifactService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeArtifact add(@RequestBody DeploymentNodeArtifact deploymentNodeArtifact){
		return service.create(deploymentNodeArtifact);
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
	public DeploymentNodeArtifact edit(@RequestBody DeploymentNodeArtifact deploymentNodeArtifact){
		return service.update(deploymentNodeArtifact);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET )
	public List<DeploymentNodeArtifact> getByNodeId(@RequestParam String nodeId){
		return service.getByNodeId(nodeId);
	}
}
