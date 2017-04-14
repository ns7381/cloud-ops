package com.cloud.ops.controller;

import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageStatus;
import com.cloud.ops.entity.Resource.ResourcePackageType;
import com.cloud.ops.service.ResourcePackageService;
import com.cloud.ops.service.ResourcePackageServiceTool;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.utils.ThreadWithEntity;
import com.cloud.ops.configuration.ws.WebSocketConstants;
import com.cloud.ops.configuration.ws.CustomWebSocketHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/resource-packages")
public class ResourcePackageController {

    @Autowired
    private ResourcePackageService service;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;
    @Autowired
    private ApplicationContext context;

    @RequestMapping(method = RequestMethod.POST)
    public ResourcePackage create(@RequestBody ResourcePackage version) {
        return service.create(version);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean delete(@PathVariable String id) {
        service.delete(id);
        return Boolean.TRUE;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResourcePackage update(@RequestBody ResourcePackage version) {
        return service.update(version);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResourcePackage get(@PathVariable String id) {
        return service.get(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ResourcePackage> getList(@RequestParam Map<String, Object> params) {
        return service.getList(params);
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response,
                         @PathVariable("id") String id) {
        ResourcePackage resourcePackage = service.get(id);
        try {
            // 清空response
            response.reset();
            // 设置response的Header
            response.setContentType("licenseInfo/octet-stream;charset=UTF-8");
            File file = new File(resourcePackage.getWarPath());
            response.setHeader("Content-Disposition", "attachment;filename="
                    + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
            FileUtils.copyFile(file, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/patch", method = RequestMethod.POST)
    public ResourcePackage patch(
            @RequestParam("deploymentId") String deploymentId,
            @RequestParam("prePackageId") String prePackageId,
            @RequestParam("nextPackageId") String nextPackageId) {

        final ResourcePackage prePackage = service.get(prePackageId);
        final ResourcePackage nextPackage = service.get(nextPackageId);

        final String uploadPath = fileStore.makeFile(FileStore.PACKAGE_FILE_PATH + UUID.randomUUID().toString() + File.separator)
                + nextPackage.getVersion() + "-patch";
        ResourcePackage patchPackage = new ResourcePackage();
        patchPackage.setType(ResourcePackageType.PATCH);
        patchPackage.setApplicationId(deploymentId);
        patchPackage.setName(nextPackage.getVersion() + "-patch");
        patchPackage.setVersion(nextPackage.getVersion() + "-patch");
        patchPackage.setDescription(prePackage.getVersion() + "版本至" + nextPackage.getVersion() + "版本的patch");
        patchPackage.setStatus(ResourcePackageStatus.COMPARE);
        service.create(patchPackage);
        new ThreadWithEntity<ResourcePackage>(patchPackage) {
            @Override
            public void run(ResourcePackage entity) {
                ResourcePackageServiceTool service = context.getBean(ResourcePackageServiceTool.class);
                String patchPath = fileStore.compareWar(prePackage.getWarPath(), nextPackage.getWarPath(), uploadPath);
                entity.setWarPath(patchPath);
                entity.setStatus(ResourcePackageStatus.FINISH);
                service.save(entity);
                webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
            }
        }.start();
        return patchPackage;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResourcePackage uploadWar(@RequestParam("file") final MultipartFile file,
                                           @RequestParam("version") String version,
                                           @RequestParam("applicationId") String applicationId) {
        final ResourcePackage resourcePackage = new ResourcePackage();
        resourcePackage.setType(ResourcePackageType.WAR);
        resourcePackage.setVersion(version);
        resourcePackage.setApplicationId(applicationId);
        resourcePackage.setStatus(ResourcePackageStatus.SAVING);
        if (file != null && !file.getOriginalFilename().trim().equals("")) {
            service.create(resourcePackage);
            String uploadPath = fileStore.makeFile(FileStore.PACKAGE_FILE_PATH + UUID.randomUUID().toString() + File.separator);
            String fileName = file.getOriginalFilename();
            final String filePath = uploadPath + fileName;
            new ThreadWithEntity<ResourcePackage>(resourcePackage) {
                @Override
                public void run(ResourcePackage entity) {
                    File destination = new File(filePath);
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), destination);
                    } catch (IOException e) {
                        throw new RuntimeException("保存war包失败！", e);
                    }
                    entity.setWarPath(destination.getAbsolutePath());
                    entity.setStatus(ResourcePackageStatus.FINISH);
                    service.create(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                }
            }.start();
        } else {
            throw new RuntimeException("war包为空！");
        }
        return resourcePackage;
    }

    @RequestMapping(value = "/git", method = RequestMethod.PUT)
    public ResourcePackage packageWar(@RequestBody ResourcePackage resourcePackage) {
        return service.packageWar(resourcePackage);
    }
}
