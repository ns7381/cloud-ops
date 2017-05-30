package com.cloud.ops.common.store;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class LocalFileStore implements FileStore{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String storeFile(InputStream data, String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            FileUtils.copyInputStreamToFile(data, path);
        } catch (IOException e) {
            logger.error("store file error: ", e);
        }
        return filePath;
    }

    @Override
    public void delete(String filePath) {
        try {
            FileUtils.forceDelete(new File(filePath));
        } catch (IOException e) {
            logger.error("delete file error: ", e);
        }
    }
}