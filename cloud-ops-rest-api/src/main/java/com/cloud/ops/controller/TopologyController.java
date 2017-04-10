package com.cloud.ops.controller;

import com.cloud.ops.entity.topology.Topology;
import com.cloud.ops.service.TopologyService;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.toscamodel.INodeTemplate;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/topologies")
@Api(value = "", description = "Operations on Topologies")
public class TopologyController {

    @Autowired
    private TopologyService service;
    @Autowired
    private FileStore fileStore;

    @PostMapping
    public Topology create(@RequestBody Topology deploymentTopology) {
        return service.create(deploymentTopology);
    }

    @PostMapping(value = "/{id}/upload")
    public Topology upload(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        Topology topology = service.get(id);
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            fileStore.delete(topology.getYamlFilePath());
        }
        String filePath = fileStore.storeFile(file.getInputStream(), FileStore.TOPOLOGY_FILE_PATH +
                topology.getName() + File.separator + file.getOriginalFilename());
        topology.setYamlFilePath(filePath);
        return service.update(id, topology);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @PutMapping(value = "/{id}")
    public Topology update(@PathVariable String id, @RequestBody Topology deploymentTopology) {
        return service.update(id, deploymentTopology);
    }

    @GetMapping(value = "/{id}")
    public Topology get(@PathVariable String id) {
        return service.get(id);
    }

    @GetMapping
    public List<Topology> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/computes")
    public List<Topology> getListWithComputes() {
        return service.getListWithComputes();
    }

}
