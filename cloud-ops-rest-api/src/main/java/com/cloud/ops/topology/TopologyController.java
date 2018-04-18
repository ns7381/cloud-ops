package com.cloud.ops.topology;

import com.cloud.ops.common.store.FileStore;
import com.cloud.ops.core.model.topology.TopologyEntity;
import com.cloud.ops.core.topology.TopologyService;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/topologies")
@Api(value = "", description = "Operations on Topologies")
public class TopologyController {
    @Value("${cloud-ops.file.topology}")
    private static String TOPOLOGY_FILE_PATH;
    @Autowired
    private TopologyService service;
    @Autowired
    private FileStore fileStore;

    @PostMapping
    public TopologyEntity create(@RequestBody TopologyEntity deploymentTopology) {
        return service.create(deploymentTopology);
    }

    @PostMapping(value = "/{id}/upload")
    public TopologyEntity upload(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        TopologyEntity topology = service.get(id);
        if (StringUtils.isNotBlank(topology.getYamlFilePath())) {
            fileStore.delete(topology.getYamlFilePath());
        }
        String filePath = TOPOLOGY_FILE_PATH + File.separator +
                topology.getName() + File.separator + file.getOriginalFilename();
        fileStore.storeFile(file.getInputStream(), filePath);
        topology.setYamlFilePath(filePath);
        return service.update(id, topology);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @PutMapping(value = "/{id}")
    public TopologyEntity update(@PathVariable String id, @RequestBody TopologyEntity deploymentTopology) {
        return service.update(id, deploymentTopology);
    }

    @GetMapping(value = "/{id}")
    public TopologyEntity get(@PathVariable String id) throws IOException {
        TopologyEntity topology = service.get(id);
        topology.setFileContents(IOUtils.readLines(new FileReader(topology.getYamlFilePath())));
        return topology;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<TopologyEntity> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/list-with-context")
    public List<TopologyEntity> getListWithContext() {
        return service.getListWithContext();
    }

}
