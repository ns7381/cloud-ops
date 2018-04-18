package com.cloud.ops.resource;

import com.cloud.ops.common.exception.OpsException;
import com.cloud.ops.common.store.FileStore;
import com.cloud.ops.common.utils.FileHelper;
import com.cloud.ops.common.utils.ThreadWithEntity;
import com.cloud.ops.common.ws.CustomWebSocketHandler;
import com.cloud.ops.common.ws.WebSocketConstants;
import com.cloud.ops.core.model.Resource.ResourcePackage;
import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.model.Resource.ResourcePackageStatus;
import com.cloud.ops.core.model.Resource.ResourcePackageType;
import com.cloud.ops.core.resource.ResourcePackageConfigService;
import com.cloud.ops.core.resource.ResourcePackageService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.cloud.ops.common.ws.WebSocketConstants.PACKAGE_STATUS;

@RestController
@RequestMapping(value = "/resource-packages")
public class ResourcePackageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePackageController.class);
    @Value("${cloud-ops.file.package}")
    private String PACKAGE_FILE_PATH;
    @Autowired
    private ResourcePackageConfigService configService;
    @Autowired
    private ResourcePackageService service;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;

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
    public List<ResourcePackage> findByApplicationId(@RequestParam String applicationId) {
        return service.findByApplicationId(applicationId);
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response,
                         @PathVariable("id") String id) {
        ResourcePackage resourcePackage = service.get(id);
        try {
            response.reset();
            response.setContentType("licenseInfo/octet-stream;charset=UTF-8");
            File file = new File(resourcePackage.getWarPath());
            response.setHeader("Content-Disposition", "attachment;filename="
                    + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
            FileUtils.copyFile(file, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("file download error: ", e);
        }
    }

    @RequestMapping(value = "/patch", method = RequestMethod.POST)
    public ResourcePackage patch(
            @RequestParam("applicationId") String applicationId,
            @RequestParam("prePackageId") String prePackageId,
            @RequestParam("nextPackageId") String nextPackageId) {

        final ResourcePackage prePackage = service.get(prePackageId);
        final ResourcePackage nextPackage = service.get(nextPackageId);

        final String uploadPath = PACKAGE_FILE_PATH + File.separator + UUID.randomUUID().toString() + File.separator
                + nextPackage.getVersion() + "-patch";
        ResourcePackage patchPackage = new ResourcePackage();
        patchPackage.setType(ResourcePackageType.PATCH);
        patchPackage.setApplicationId(applicationId);
        patchPackage.setName(prePackage.getVersion() + "TO" + nextPackage.getVersion());
        patchPackage.setVersion(nextPackage.getVersion());
        patchPackage.setDescription(prePackage.getVersion() + "版本至" + nextPackage.getVersion() + "版本的patch");
        patchPackage.setStatus(ResourcePackageStatus.COMPARE);
        service.create(patchPackage);
        new ThreadWithEntity<ResourcePackage>(patchPackage) {
            @Override
            public void run(ResourcePackage entity) {
                String patchPath = FileHelper.compareWar(prePackage.getWarPath(), nextPackage.getWarPath(), uploadPath);
                entity.setWarPath(patchPath);
                entity.setStatus(ResourcePackageStatus.FINISH);
                service.update(entity);
                webSocketHandler.sendMsg(PACKAGE_STATUS, entity);
            }
        }.start();
        return patchPackage;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResourcePackage uploadWar(@RequestParam("file") MultipartFile file,
                                     @RequestParam("version") String version,
                                     @RequestParam("type") String type,
                                     @RequestParam("applicationId") String applicationId) {
        final ResourcePackage resourcePackage = new ResourcePackage();
        resourcePackage.setType(ResourcePackageType.valueOf(type));
        resourcePackage.setName(version);
        resourcePackage.setVersion(version);
        resourcePackage.setApplicationId(applicationId);
        if (file != null && !file.getOriginalFilename().trim().equals("")) {
            String uploadPath = PACKAGE_FILE_PATH + File.separator + UUID.randomUUID().toString() + File.separator;
            String fileName = file.getOriginalFilename();
            final String filePath = uploadPath + fileName;
            resourcePackage.setStatus(ResourcePackageStatus.SAVING);
            service.create(resourcePackage);
            new ThreadWithEntity<ResourcePackage>(resourcePackage) {
                @Override
                public void run(ResourcePackage entity) {
                    File destination = new File(filePath);
                    try {
                        fileStore.storeFile(file.getInputStream(), filePath);
                    } catch (IOException e) {
                        throw new OpsException("保存war包失败！", e);
                    }
                    entity.setWarPath(destination.getAbsolutePath());
                    entity.setStatus(ResourcePackageStatus.FINISH);
                    service.create(entity);
                    webSocketHandler.sendMsg(PACKAGE_STATUS, entity);
                }
            }.start();
        } else {
            throw new OpsException("war包为空！");
        }
        return resourcePackage;
    }

    @RequestMapping(value = "/git", method = RequestMethod.PUT)
    public ResourcePackage packageWar(@RequestBody ResourcePackage resourcePackage) {
        ResourcePackageConfig config = configService.findByApplicationId(resourcePackage.getApplicationId());
        resourcePackage.setConfig(config);
        assert StringUtils.isNotBlank(resourcePackage.getBuild());
        resourcePackage.setName(resourcePackage.getVersion());
        resourcePackage.setType(ResourcePackageType.WAR);
        resourcePackage.setStatus(ResourcePackageStatus.CLONING);
        this.create(resourcePackage);
        new ThreadWithEntity<ResourcePackage>(resourcePackage) {

            @Override
            public void run(ResourcePackage entity) {
                service.packageWar(entity);
            }
        }.start();
        return resourcePackage;
    }


}
