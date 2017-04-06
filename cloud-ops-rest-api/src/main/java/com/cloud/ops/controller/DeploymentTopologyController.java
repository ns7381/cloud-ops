package com.cloud.ops.controller;

import com.cloud.ops.entity.deployment.DeploymentTopology;
import com.cloud.ops.service.DeploymentTopologyService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping(value = "/topologies")
//@Api(value = "", description = "Operations on Topologies")
public class DeploymentTopologyController {

    @Autowired
    private DeploymentTopologyService service;

    /**
     * 新增
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public DeploymentTopology add(@RequestBody DeploymentTopology deploymentTopology) {
        return service.create(deploymentTopology);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    /**
     * 编辑
     *
     * @return
     */
    @PutMapping(value = "/{id}")
    public DeploymentTopology edit(@PathVariable String id, @RequestBody DeploymentTopology deploymentTopology) {
        return service.update(id, deploymentTopology);
    }

    /**
     * 获取所有数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<DeploymentTopology> getAll() {
        return service.getAll();
    }

}
