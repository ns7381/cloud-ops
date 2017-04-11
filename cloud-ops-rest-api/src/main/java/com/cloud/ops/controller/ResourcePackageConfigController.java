package com.cloud.ops.controller;

import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.service.ResourcePackageConfigService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping(value = "/package-configs")
public class ResourcePackageConfigController {

    @Autowired
    private ResourcePackageConfigService service;


    @RequestMapping(method = RequestMethod.POST)
    public ResourcePackageConfig create(@RequestBody ResourcePackageConfig entity) {
        return service.create(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResourcePackageConfig update(@RequestBody ResourcePackageConfig entity) {
        return service.update(entity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResourcePackageConfig get(@PathVariable String id) {
        return service.get(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResourcePackageConfig findByApplicationId(@RequestParam String applicationId) {
        return service.findByApplicationId(applicationId);
    }

    @PutMapping(value = "/branches")
    public List<String> getBranches(@RequestBody ResourcePackageConfig entity) {
        List<String> results = new ArrayList<>();
        try {
            Collection<Ref> refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setTags(true)
                    .setRemote(entity.getGitUrl())
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(entity.getGitUsername(), entity.getGitPassword()))
                    .call();
            for (Ref ref : refs) {
                results.add(ref.getName());
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return results;
    }
}
