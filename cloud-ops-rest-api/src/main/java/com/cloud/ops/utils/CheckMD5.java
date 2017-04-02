package com.cloud.ops.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/2/3.
 */
public class CheckMD5 {
    static MessageDigest MD5 = null;


    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) {
            ne.printStackTrace();
        }
    }


    /**
     * 对一个文件获取md5值
     * @return md5串
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }

    public static void main(String[] args) {
/*        System.out.println(getMD5(new File("E:\\iop-ops\\package\\25c2767fb2204029a00a17a36449ce0c\\69638a08-57ee-4b66-9090-e2c340a94d1b\\cloud_web\\WEB-INF\\lib\\cloud-service-factory-3.1.0.jar")));
        System.out.println(getMD5(new File("E:\\iop-ops\\package\\2ecf6abc0bc34ce4adba1dc1c23edcbf\\aeae0a973dcb411da6775c5f6ad01b88\\cloud-service-factory-3.1.0.jar")));
        System.out.println(getMD5(new File("E:\\cloud-service-factory-3.1.0.jar")));*/
//        System.out.println(getMD5(new File("E:\\iop-ops\\package\\25c2767fb2204029a00a17a36449ce0c\\69638a08-57ee-4b66-9090-e2c340a94d1b\\cloud_web\\app\\app.js")));
        System.out.println(getMD5(new File("E:\\app.js")));
        System.out.println(getMD5(new File("E:\\update-version\\app.js")));
        System.out.println(getMD5(new File("E:\\update-install\\app.js")));
//        System.out.println(getMD5(new File("E:\\iop-ops\\patch\\25c2767fb2204029a00a17a36449ce0c\\6ca6de1e107b4105bc6f82a4b483d526\\af90bbf7174e42c58ac99a84e5cff8ee\\app\\app.js")));
    }
}
