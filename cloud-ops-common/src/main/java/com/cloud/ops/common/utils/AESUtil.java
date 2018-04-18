package com.cloud.ops.common.utils;

import com.cloud.ops.common.exception.OpsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES 对称加密算法
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AESUtil {

    private static Log log = LogFactory.getLog(AESUtil.class);

    public static String createKey(String seed, String fileName) {
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(seed.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            if (!FileHelper.createFile(fileName)) {
                throw new OpsException("yaml file create error");
            }

            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(secretKey);
            oos.flush();
            oos.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Generate key error: ", e);
        }
        return fileName;
    }

}

