package com.cloud.ops.store;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Nathan on 2017/4/6.
 */
public interface FileStore {
    String PROJECT_PATH = System.getProperty("user.dir").substring(0,
            System.getProperty("user.dir").indexOf(File.separator));
    String TOPOLOGY_FILE_PATH = PROJECT_PATH + File.separator + "iop-ops" +
            File.separator + "topology" + File.separator;
    String storeFile(InputStream data, String filePath);
    void delete(String filePath);
}
