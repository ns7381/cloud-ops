package com.cloud.ops.resource;

import com.cloud.ops.common.cmd.LocalExecuteCommand;
import com.cloud.ops.common.utils.FileHelper;
import com.cloud.ops.common.utils.ThreadWithEntity;
import com.cloud.ops.core.model.Resource.ResourcePackage;
import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.model.Resource.ResourcePackageStatus;
import com.cloud.ops.core.model.Resource.ResourcePackageType;
import com.cloud.ops.core.resource.ResourcePackageConfigService;
import com.cloud.ops.core.resource.ResourcePackageService;
import com.cloud.ops.common.store.FileStore;
import com.cloud.ops.websocket.CustomWebSocketHandler;
import com.cloud.ops.websocket.WebSocketConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/resource-packages")
public class ResourcePackageController {
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
        patchPackage.setName(nextPackage.getVersion() + "-patch");
        patchPackage.setVersion(nextPackage.getVersion() + "-patch");
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
                webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
            }
        }.start();
        return patchPackage;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResourcePackage uploadWar(@RequestParam("file") final MultipartFile file,
                                           @RequestParam("version") String version,
                                           @RequestParam("type") String type,
                                           @RequestParam("applicationId") String applicationId) {
        final ResourcePackage resourcePackage = new ResourcePackage();
        resourcePackage.setType(ResourcePackageType.valueOf(type));
        resourcePackage.setVersion(version);
        resourcePackage.setApplicationId(applicationId);
        if (file != null && !file.getOriginalFilename().trim().equals("")) {
            String uploadPath = PACKAGE_FILE_PATH + File.separator + UUID.randomUUID().toString() + File.separator;
            String fileName = file.getOriginalFilename();
            final String filePath = uploadPath + fileName;
            try {
                fileStore.storeFile(file.getInputStream(), filePath);
            } catch (IOException e) {
                throw new RuntimeException("保存war包失败！", e);
            }
            resourcePackage.setWarPath((new File(filePath)).getAbsolutePath());
            resourcePackage.setStatus(ResourcePackageStatus.SAVING);
            service.create(resourcePackage);
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
        ResourcePackageConfig config = configService.findByApplicationId(resourcePackage.getApplicationId());
        resourcePackage.setConfig(config);
        assert StringUtils.isNotBlank(resourcePackage.getBuild());
        resourcePackage.setType(ResourcePackageType.WAR);
        resourcePackage.setStatus(ResourcePackageStatus.CLONING);
        this.create(resourcePackage);
        new ThreadWithEntity<ResourcePackage>(resourcePackage) {

            @Override
            public void run(ResourcePackage entity) {
                ResourcePackageConfig config = entity.getConfig();
                File localPath = null;
                try {
                    localPath = File.createTempFile("TestGitRepository", "");
                    if(!localPath.delete()) {
                        throw new IOException("Could not delete temporary file " + localPath);
                    }
                    System.out.println("clone from " + config.getGitUrl() + " to " + localPath);
                    Git result = Git.cloneRepository()
                            .setURI(config.getGitUrl())
                            .setDirectory(localPath)
                            .setCloneSubmodules(true)
                            .setBranch(config.getBranch())
                            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGitUsername(), config.getGitPassword()))
                            .call();
                    System.out.println("Having repository: " + result.getRepository().getDirectory());
                    entity.setStatus(ResourcePackageStatus.BUILDING);
                    service.update(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);

                    File dir = result.getRepository().getDirectory().getParentFile();
                    LocalExecuteCommand.execute(entity.getBuild(), dir);
                    entity.setStatus(ResourcePackageStatus.SAVING);
                    service.update(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);

                    String uploadPath = PACKAGE_FILE_PATH + UUID.randomUUID().toString();
                    FileUtils.forceMkdir(new File(uploadPath));
                    List<File> files = (List<File>) FileUtils.listFiles(new File(dir + File.separator + config.getBuildDir()), new String[]{"war","zip"}, false);
                    assert files.size() > 0;
                    File destFile = new File(uploadPath);
                    FileUtils.copyFileToDirectory(files.get(0), destFile);
                    entity.setWarPath(destFile + File.separator + files.get(0).getName());
                    entity.setStatus(ResourcePackageStatus.FINISH);
                    service.update(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                    deleteFile(localPath);
                } catch (Exception e) {
                    deleteFile(localPath);
                    e.printStackTrace();
                    entity.setStatus(ResourcePackageStatus.FAIL);
                    service.update(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                }
            }
        }.start();
        return resourcePackage;
    }

    void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    deleteFile(child);
                }
            }
            file.delete();
        }
    }
}
