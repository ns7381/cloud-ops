package com.cloud.ops.store;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.*;

@Component
public class LocalFileStore implements FileStore{
    private final Logger logger = LoggerFactory.getLogger(getClass());
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

    public static void main(String[] args) {
        System.out.println(TOPOLOGY_FILE_PATH);
    }

}