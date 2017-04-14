package com.cloud.ops.service;

import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.entity.Resource.ResourcePackageType;
import com.cloud.ops.repository.ResourcePackageRepository;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageStatus;
import com.cloud.ops.store.FileStore;
import com.cloud.ops.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.cloud.ops.configuration.ws.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ResourcePackageService {
    @Autowired
    private ResourcePackageRepository dao;
    @Autowired
    private ResourcePackageConfigService configService;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;
    @Autowired
    private ApplicationContext context;

    public ResourcePackage get(String id) {
        return dao.findOne(id);
    }

    public ResourcePackage create(ResourcePackage version) {
        dao.save(version);
        return version;
    }
    public void delete(String id) {
        dao.delete(id);
        try {
            String warPath = this.get(id).getWarPath();
            if (StringUtils.isNotBlank(warPath)) {
                org.apache.commons.io.FileUtils.forceDeleteOnExit(new File(warPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<ResourcePackage> getList(Map<String, Object> params) {
        return dao.findByApplicationId((String) params.get("applicationId"));
    }

    public ResourcePackage update(ResourcePackage version) {
        ResourcePackage db = this.get(version.getId());
        BeanUtils.copyNotNullProperties(version, db);
        dao.save(db);
        return db;
    }

    public ResourcePackage packageWar(ResourcePackage resourcePackage) {
        ResourcePackageConfig config = configService.findByApplicationId(resourcePackage.getApplicationId());
        resourcePackage.setConfig(config);
        assert StringUtils.isNotBlank(config.getBuild());
        resourcePackage.setType(ResourcePackageType.WAR);
        resourcePackage.setStatus(ResourcePackageStatus.CLONING);
        this.create(resourcePackage);
        new ThreadWithEntity<ResourcePackage>(resourcePackage) {

            @Override
            public void run(ResourcePackage entity) {
                ResourcePackageServiceTool service = context.getBean(ResourcePackageServiceTool.class);
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
                    /*Git result = Git.open(new File(repository.getLocalDir()));
                    String branch = entity.getBranch();
                    result.branchCreate()
                            .setName(branch)
                            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                            .setStartPoint("refs/remotes/origin/" + branch)
                            .setForce(true)
                            .call();
                    result.checkout().setName(branch).call();
                    result.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(repository.getUsername(), repository.getPassword())).call();
                    System.out.println("pull success: " + result.getRepository().getDirectory());*/
                    entity.setStatus(ResourcePackageStatus.BUILDING);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);

                    File dir = result.getRepository().getDirectory().getParentFile();
                    LocalExecuteCommand.execute(config.getBuild(), dir);
                    entity.setStatus(ResourcePackageStatus.SAVING);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);

                    String uploadPath = FileStore.PACKAGE_FILE_PATH + UUID.randomUUID().toString();
                    FileUtils.forceMkdir(new File(uploadPath));
                    List<File> files = (List<File>) FileUtils.listFiles(new File(dir + File.separator + config.getBuildDir()), new String[]{"war"}, false);
                    assert files.size() > 0;
                    File destFile = new File(uploadPath);
                    FileUtils.copyFileToDirectory(files.get(0), destFile);
                    entity.setWarPath(destFile + File.separator + files.get(0).getName());
                    entity.setStatus(ResourcePackageStatus.FINISH);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                    deleteFile(localPath);
                } catch (Exception e) {
                    deleteFile(localPath);
                    e.printStackTrace();
                    entity.setStatus(ResourcePackageStatus.FAIL);
                    service.save(entity);
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

    public void updateDeployStatus(String packageId) {
        for (ResourcePackage resourcePackage : dao.findAll()) {
            if (ResourcePackageStatus.DEPLOY.equals(resourcePackage.getStatus())) {
                resourcePackage.setStatus(ResourcePackageStatus.FINISH);
                dao.save(resourcePackage);
            }
        }
        ResourcePackage db = dao.findOne(packageId);
        db.setStatus(ResourcePackageStatus.DEPLOY);
        dao.save(db);
    }
}
