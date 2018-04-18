package com.cloud.ops.resource;

import com.cloud.ops.core.model.Resource.ResourcePackageFile;
import com.cloud.ops.core.resource.ResourcePackageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/package-files")
public class ResourcePackageFileController {

    @Autowired
    private ResourcePackageFileService service;


    @RequestMapping(method = RequestMethod.POST)
    public ResourcePackageFile create(@RequestBody ResourcePackageFile entity) {
        return service.create(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResourcePackageFile update(@RequestBody ResourcePackageFile entity) {
        return service.update(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResourcePackageFile get(@PathVariable String id) {
        return service.get(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public List<ResourcePackageFile> findByApplicationId(@RequestParam String applicationId) {
        return service.findByApplicationId(applicationId);
    }

}
