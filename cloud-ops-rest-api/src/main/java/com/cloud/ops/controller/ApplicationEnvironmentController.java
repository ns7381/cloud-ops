package com.cloud.ops.controller;

import com.cloud.ops.entity.application.ApplicationEnvironment;
import com.cloud.ops.service.ApplicationEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/environments")
public class ApplicationEnvironmentController {

    @Autowired
    private ApplicationEnvironmentService service;


    @RequestMapping(method = RequestMethod.POST)
    public ApplicationEnvironment create(@RequestBody ApplicationEnvironment applicationEnvironment) {
        return service.add(applicationEnvironment);
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
    @RequestMapping(method = RequestMethod.PUT)
    public ApplicationEnvironment edit(@RequestBody ApplicationEnvironment applicationEnvironment) {
        return service.edit(applicationEnvironment);
    }

    /**
     * 获取所有数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<ApplicationEnvironment> getAll() {
        return service.getAll();
    }

}
