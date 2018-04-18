package com.cloud.ops.topology;

import com.cloud.ops.core.model.topology.TopologyArchive;
import com.cloud.ops.core.topology.TopologyArchiveService;
import com.cloud.ops.core.topology.TopologyService;
import com.cloud.ops.common.store.FileStore;
import com.google.common.io.Files;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/topologies/{topologyId}/archives")
public class TopologyArchiveController {
    @Value("${cloud-ops.file.topology}")
    private String TOPOLOGY_FILE_PATH;
    @Autowired
    private TopologyArchiveService service;
    @Autowired
    private TopologyService topologyService;
    @Autowired
    private FileStore fileStore;

    @ApiOperation(value = "create topology's archive.", authorizations = { @Authorization("ADMIN") })
    @PostMapping(headers = "content-type=multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseBody
    public TopologyArchive create(@RequestParam("file") MultipartFile file, @PathVariable String topologyId) throws IOException {
        String name = topologyService.get(topologyId).getName();
        String filePath = TOPOLOGY_FILE_PATH + File.separator + name + File.separator + file.getOriginalFilename();
        fileStore.storeFile(file.getInputStream(), filePath);
        TopologyArchive archive = new TopologyArchive();
        archive.setName(file.getOriginalFilename());
        archive.setTopologyId(topologyId);
        archive.setFilePath(filePath);
        return service.create(archive);
    }

    @ApiOperation(value = "create topology's archive.", authorizations = { @Authorization("ADMIN") })
    @PostMapping(value = "{id}/override")
    @PreAuthorize("hasAuthority('ADMIN')")
    public TopologyArchive update(@RequestParam("file") MultipartFile file, @PathVariable String topologyId,
                                  @PathVariable String id) throws IOException {
        TopologyArchive archive = service.get(id);
        if (StringUtils.isNotBlank(archive.getFilePath())) {
            fileStore.delete(archive.getFilePath());
        }
        String name = topologyService.get(topologyId).getName();
        String filePath = TOPOLOGY_FILE_PATH + File.separator + name + File.separator + file.getOriginalFilename();
        fileStore.storeFile(file.getInputStream(), filePath);
        archive.setFilePath(filePath);
        return service.update(archive);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String topologyId, @PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public TopologyArchive update(@PathVariable String topologyId, @RequestBody TopologyArchive entity) {
        entity.setId(topologyId);
        return service.update(entity);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<TopologyArchive> getByTopologyId(@PathVariable String topologyId) throws IOException {
        List<TopologyArchive> archives = service.findByTopologyId(topologyId);
        for (TopologyArchive archive : archives) {
            if (Files.getFileExtension(archive.getFilePath()).equals("sh")) {
                archive.setFileContents(IOUtils.readLines(new FileInputStream(archive.getFilePath()), "UTF-8"));
            }
        }
        return archives;
    }
}
