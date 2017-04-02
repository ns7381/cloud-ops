package com.cloud.ops.service;

import com.cloud.ops.dao.ResourcePackageDao;
import com.cloud.ops.entity.Resource.Repository;
import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageStatus;
import com.cloud.ops.utils.*;
import com.cloud.ops.ws.WebSocketConstants;
import com.cloud.ops.ws.WebSocketHandler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResourcePackageService {
    @Autowired
    private ResourcePackageDao dao;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private WebSocketHandler webSocketHandler;

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
        return dao.findByDeploymentId((String) params.get("deploymentId"));
    }

    public ResourcePackage update(ResourcePackage version) {
        Assert.notNull(version.getId());
        ResourcePackage db = this.get(version.getId());
        BeanUtils.copyNotNullProperties(version, db);
        dao.save(db);
        return db;
    }

    public void packageWar(ResourcePackage resourcePackage) {
        assert StringUtils.isNotBlank(resourcePackage.getBuild());
        final Repository repository = repositoryService.get(resourcePackage.getRepositoryId());

        resourcePackage.setStatus(ResourcePackageStatus.CLONING);
        this.create(resourcePackage);
        new ThreadWithEntity<ResourcePackage>(resourcePackage) {

            @Override
            public void run(ResourcePackage entity) {
                ResourcePackageServiceTool service = SpringContextHolder.getBean(ResourcePackageServiceTool.class);
                File localPath = null;
                try {
                    localPath = File.createTempFile("TestGitRepository", "");
                    if(!localPath.delete()) {
                        throw new IOException("Could not delete temporary file " + localPath);
                    }
                    System.out.println("clone from " + repository.getRepositoryUrl() + " to " + localPath);
                    Git result = Git.cloneRepository()
                            .setURI(repository.getRepositoryUrl())
                            .setDirectory(localPath)
                            .setCloneSubmodules(true)
                            .setBranch(entity.getBranch())
                            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(repository.getUsername(), repository.getPassword()))
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
                    LocalExecuteCommand.execute(entity.getBuild(), dir);
                    entity.setStatus(ResourcePackageStatus.SAVING);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);

                    String uploadPath = FileUtils.getPackageFilePath(repository.getId(), entity.getId());
                    List<File> files = FileUtils.findFile(new File(dir + File.separator + entity.getBuildDir()));
                    assert files.size() > 0;
                    File destFile = new File(uploadPath);
                    org.apache.commons.io.FileUtils.copyFileToDirectory(files.get(0), destFile);
                    entity.setWarPath(destFile + File.separator + files.get(0).getName());
                    entity.setStatus(ResourcePackageStatus.FINISH);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                    FileUtils.deleteFile(localPath);
                } catch (Exception e) {
                    FileUtils.deleteFile(localPath);
                    e.printStackTrace();
                    entity.setStatus(ResourcePackageStatus.FAIL);
                    service.save(entity);
                    webSocketHandler.sendMsg(WebSocketConstants.PACKAGE_STATUS, entity);
                }
            }
        }.start();

    }
}
