package com.cloud.ops.controller;

import com.cloud.ops.entity.application.Application;
import com.cloud.ops.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/applications")
public class ApplicationController {

    @Autowired
	private ApplicationService service;

	@RequestMapping(method = RequestMethod.POST)
	public Application create(@RequestBody Application application){
		return service.create(application);
	}

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id){
        service.delete(id);
        return Boolean.TRUE;
    }

    /*@RequestMapping(value = "/{id}/interface/{interfaceId}", method = RequestMethod.PUT)
    public Boolean doInterface(@PathVariable String id, @PathVariable String interfaceId, @RequestParam Map<String, Object> params){
        return service.doInterface(id, interfaceId, params);
    }*/

    @RequestMapping(value = "/{id}/node/{nodeId}/deploy/{packageId}", method = RequestMethod.PUT)
    public Boolean deploy(@PathVariable String id, @PathVariable String nodeId, @PathVariable String packageId){
        return service.deploy(id, nodeId, packageId);
    }

    @PutMapping(value = "/{id}/node/{nodeId}/attributes")
    public Boolean changeApplicationAttributes(@PathVariable String id, @PathVariable String nodeId, @RequestParam Map<String, Object> attributes){
        return service.changeApplicationAttributes(id, nodeId, attributes);
    }

    @GetMapping(value = "/{id}")
    public Application get(@PathVariable String id) {
        return service.get(id);
    }

	@RequestMapping(method = RequestMethod.GET )
	public List<Application> findByEnvironmentId(@RequestParam String environmentId){
		return service.findByEnvironmentId(environmentId);
	}

}
