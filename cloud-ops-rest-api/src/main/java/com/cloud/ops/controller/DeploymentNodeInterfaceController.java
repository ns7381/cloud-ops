package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentNodeInterface;
import com.cloud.ops.service.DeploymentNodeInterfaceService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/topology/node/interface")
public class DeploymentNodeInterfaceController {

    @Autowired
	private DeploymentNodeInterfaceService service;

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public DeploymentNodeInterface add(@RequestParam("file") MultipartFile file,
                                                     @RequestParam String name,
                                                     @RequestParam String deploymentNodeId,
                                                     @RequestParam String description){
        DeploymentNodeInterface nodeInterface = new DeploymentNodeInterface();
        nodeInterface.setName(name);
        nodeInterface.setDeploymentNodeId(deploymentNodeId);
        nodeInterface.setDescription(description);
        try {
            File destination = new File(com.cloud.ops.utils.FileUtils.getInterfaceFilePath() + File.separator + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), destination);
            nodeInterface.setImplementation(destination.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return service.create(nodeInterface);
	}

	/**
	 * 新增
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public DeploymentNodeInterface upload(@RequestParam("file") MultipartFile file,
                                                        @RequestParam String id){
        DeploymentNodeInterface nodeInterface = service.get(id);
        try {
            File destination = new File(com.cloud.ops.utils.FileUtils.getInterfaceFilePath() + File.separator + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), destination);
            nodeInterface.setImplementation(destination.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return service.update(nodeInterface);
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
	public DeploymentNodeInterface edit(@RequestBody DeploymentNodeInterface deploymentNodeInterface){
		return service.update(deploymentNodeInterface);
	}

	/**
	 * 获取数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<DeploymentNodeInterface> getList(@RequestParam Map<String, Object> params){
		return service.getList(params);
	}
}
