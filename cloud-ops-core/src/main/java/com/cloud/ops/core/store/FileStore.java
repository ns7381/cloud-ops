package com.cloud.ops.core.store;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Nathan on 2017/4/6.
 */
public interface FileStore {
    String USER_HOME = System.getProperty("user.home");
    String TOPOLOGY_FILE_PATH = USER_HOME + File.separator + "iop-ops" + File.separator + "topology" + File.separator;
    String PACKAGE_FILE_PATH = USER_HOME + File.separator + "iop-ops" + File.separator + "package" + File.separator;


    String storeFile(InputStream data, String filePath);

    void delete(String filePath);

    String makeFile(String filePath);

    String compareWar(String oldWarPath, String newWarPath, String outPath);

}
