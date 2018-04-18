package com.cloud.ops.application;

import com.cloud.ops.core.application.ApplicationEnvironmentService;
import com.cloud.ops.core.model.application.ApplicationEnvironment;
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
        return service.create(applicationEnvironment);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ApplicationEnvironment update(@RequestBody ApplicationEnvironment applicationEnvironment) {
        return service.update(applicationEnvironment);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ApplicationEnvironment> getAll() {
        return service.getAll();
    }

}
