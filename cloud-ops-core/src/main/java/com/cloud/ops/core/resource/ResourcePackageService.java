package com.cloud.ops.core.resource;

import com.cloud.ops.common.cmd.LocalExecute;
import com.cloud.ops.common.utils.BeanUtils;
import com.cloud.ops.common.utils.FileHelper;
import com.cloud.ops.common.utils.MD5Utils;
import com.cloud.ops.common.ws.CustomWebSocketHandler;
import com.cloud.ops.core.model.Resource.ResourcePackage;
import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.model.Resource.ResourcePackageFile;
import com.cloud.ops.core.model.Resource.ResourcePackageStatus;
import com.cloud.ops.core.resource.repository.ResourcePackageRepository;
import com.cloud.ops.dao.modal.SortConstant;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.cloud.ops.common.ws.WebSocketConstants.PACKAGE_STATUS;
import static com.cloud.ops.core.model.Resource.ResourcePackageStatus.*;

@Service
@Transactional
public class ResourcePackageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePackageService.class);
    @Value("${cloud-ops.file.package}")
    private String PACKAGE_FILE_PATH;
    @Autowired
    private CustomWebSocketHandler webSocketHandler;
    @Autowired
    private ResourcePackageFileService fileService;
    @Autowired
    private ResourcePackageRepository dao;

    public ResourcePackage get(String id) {
        return dao.findOne(id);
    }

    public ResourcePackage create(ResourcePackage version) {
        dao.save(version);
        return version;
    }

    public void delete(String id) {
        try {
            String warPath = this.get(id).getWarPath();
            if (StringUtils.isNotBlank(warPath)) {
                org.apache.commons.io.FileUtils.forceDeleteOnExit(new File(warPath));
            }
            dao.delete(id);
        } catch (IOException e) {
            LOGGER.error("file delete error: ", e);
        }
    }

    public List<ResourcePackage> findByApplicationId(String applicationId) {
        return dao.findByApplicationId(applicationId, SortConstant.CREATED_AT);
    }

    public ResourcePackage update(ResourcePackage version) {
        ResourcePackage db = this.get(version.getId());
        BeanUtils.copyNotNullProperties(version, db);
        dao.save(db);
        webSocketHandler.sendMsg(PACKAGE_STATUS, db);
        return db;
    }

    private void changeStatus(ResourcePackage version, ResourcePackageStatus status) {
        ResourcePackage db = this.get(version.getId());
        db.setStatus(status);
        dao.save(db);
    }

    public void packageWar(ResourcePackage entity) {
        ResourcePackageConfig config = entity.getConfig();
        File localPath = null;
        try {
            localPath = File.createTempFile("TestGitRepository", "");
            if (!localPath.delete()) {
                throw new IOException("Could not delete temporary file " + localPath);
            }
            Git result = Git.cloneRepository()
                    .setURI(config.getGitUrl())
                    .setDirectory(localPath)
                    .setCloneSubmodules(true)
                    .setBranch(entity.getBranch())
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGitUsername(), config.getGitPassword()))
                    .call();
            this.changeStatus(entity, BUILDING);

            File dir = result.getRepository().getDirectory().getParentFile();
            LocalExecute.execute(entity.getBuild(), dir);
            this.changeStatus(entity, SAVING);

            this.injectExternalFile(entity, dir);
            this.changeStatus(entity, FINISH);
        } catch (Exception e) {
            LOGGER.error("file clone build error: ", e);
            this.changeStatus(entity, FAIL);
        } finally {
            deleteFile(localPath);
        }
    }

    private void injectExternalFile(ResourcePackage entity, File projectDir) {
        FileOutputStream destFileOut = null;
        try {
            String uploadPath = PACKAGE_FILE_PATH + UUID.randomUUID().toString();
            FileUtils.forceMkdir(new File(uploadPath));
            File directory = new File(projectDir + File.separator + entity.getConfig().getBuildDir());
            List<File> files = (List<File>) FileUtils.listFiles(directory, new String[]{"war", "zip"}, false);
            assert !files.isEmpty();
            File targetFile = files.get(0);
            List<ResourcePackageFile> externalFiles = fileService.findByApplicationId(entity.getApplicationId());
            if (!externalFiles.isEmpty()) {
                String warUnzipPath = FileHelper.unZipFile(files.get(0).getAbsolutePath(), null);
                for (ResourcePackageFile file : externalFiles) {
                    String destFileName = warUnzipPath +File.separator + file.getName();
                    switch (file.getType()) {
                        case CIPHER:
                            FileUtils.copyFile(new File(file.getPath()), new File(destFileName));
                            break;
                        case MD5:
                            FileHelper.createFile(destFileName);
                            destFileOut = new FileOutputStream(destFileName);
                            Writer out = new OutputStreamWriter(destFileOut, "utf-8");
                            Collection<File> warFiles = FileUtils.listFiles(new File(warUnzipPath), null, false);
                            for (File warFile : warFiles) {
                                out.write(warFile.getName() + " " + MD5Utils.getMD5(warFile));
                                out.write("\n");
                            }
                            out.close();
                            break;
                    }
                }
                FileHelper.zipFile(warUnzipPath,files.get(0).getName(), uploadPath, true);
            } else {
                FileUtils.copyFileToDirectory(files.get(0), new File(uploadPath));
            }
            entity.setWarPath(uploadPath + File.separator + targetFile.getName());
            this.update(entity);
        } catch (ZipException | IOException e) {
            LOGGER.error("file close error: ", e);
        } finally {
            try {
                if (destFileOut != null) {
                    destFileOut.close();
                }
            } catch (IOException e) {
                LOGGER.error("file close error: ", e);
            }
        }
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File child : files) {
                        deleteFile(child);
                    }
                }
            }
            file.delete();
        }
    }
}
