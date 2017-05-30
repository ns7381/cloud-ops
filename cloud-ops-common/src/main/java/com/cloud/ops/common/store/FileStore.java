package com.cloud.ops.common.store;

import java.io.InputStream;

/**
 * Created by Nathan on 2017/4/6.
 */
public interface FileStore {
    String storeFile(InputStream data, String filePath);

    void delete(String filePath);

}
