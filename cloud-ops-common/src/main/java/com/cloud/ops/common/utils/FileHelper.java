package com.cloud.ops.common.utils;

import com.cloud.ops.common.exception.OpsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ningsheng on 2017/5/30.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            String destDirNameNew = destDirName + File.separator;
            dir = new File(destDirNameNew);
        }
        return dir.mkdirs();
    }

    public static String unZipFile(String zipFilePath, String destPath) throws ZipException {
        ZipFile zip = new ZipFile(zipFilePath);
        String result = destPath;
        if (result == null) {
            result = zipFilePath.substring(0, zipFilePath.lastIndexOf('.'));
            zip.extractAll(result);
        }
        return result;
    }

    public static String zipFile(String filePath, String zipFileName, String destPath, boolean deleteIfExist) throws ZipException {
        File file = new File(filePath);
        String name = file.getName();
        if (file.isFile() && name.substring(name.lastIndexOf('.'), name.length()).equalsIgnoreCase(".zip")) {
            return filePath;
        }
        if (destPath == null) {
            destPath = file.getParent();
        }
        if (zipFileName == null) {
            if (file.isFile()) {
                zipFileName = name.substring(0, name.lastIndexOf('.')) + ".zip";
            } else {
                zipFileName = name + ".zip";
            }
        }
        //如果目标压缩包已存在，删除压缩包
        File zipFile = new File(destPath + File.separator + zipFileName);
        if (zipFile.exists() && zipFile.isFile() && deleteIfExist) {
            zipFile.delete();
        }
        ZipFile zip = new ZipFile(destPath + File.separator + zipFileName);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        if (file.isFile()) {
            zip.addFile(file, parameters);
        } else {
            for (File child : file.listFiles()) {
                if (child.isFile()) {
                    zip.addFile(child, parameters);
                } else {
                    zip.addFolder(child, parameters);
                }
            }
        }

        return destPath + File.separator + zipFileName;
    }

    public static String compareWar(String oldWarPath, String newWarPath, String outPath) {
        if (!createDir(outPath)) {
            throw new OpsException("patch file directory create error");
        }
        String oldPath = null;
        String newPath = null;
        String patchPath = null;

        File unchangedListFile = new File(outPath + "/files-to-check.txt");
        if (!unchangedListFile.exists()) {
            createFile(outPath + "/files-to-check.txt");
        }
        File deleteListFile = new File(outPath + "/files-to-del.txt");
        if (!deleteListFile.exists()) {
            createFile(outPath + "/files-to-del.txt");
        }
        try (FileOutputStream unchangeOut = new FileOutputStream(unchangedListFile);
             FileOutputStream deleteOut = new FileOutputStream(deleteListFile)) {
            oldPath = unZipFile(oldWarPath, null);
            newPath = unZipFile(newWarPath, null);
            Iterator newFileIterator = FileUtils.iterateFiles(new File(newPath), null, true);
            Iterator oldFileIterator = FileUtils.iterateFiles(new File(oldPath), null, true);
            Writer unchangedListFileOut = new OutputStreamWriter(unchangeOut, "utf-8");
            while (newFileIterator.hasNext()) {
                String nPathStr = newFileIterator.next().toString();
                File newFile = new File(nPathStr);
                File oldFile = new File(nPathStr.replace(newPath, oldPath));
                if (!oldFile.exists()) {
                    File outFile = new File(nPathStr.replace(newPath, outPath));
                    FileUtils.copyFile(newFile, outFile);
                } else {
                    String newMD5 = MD5Utils.getMD5(newFile);
                    String oldMD5 = MD5Utils.getMD5(oldFile);
                    if (!StringUtils.equals(newMD5, oldMD5)) {
                        File outFile = new File(nPathStr.replace(newPath, outPath));
                        FileUtils.copyFile(newFile, outFile);
                    } else {
                        unchangedListFileOut.write(newMD5 + "  " + nPathStr.substring(newPath.length() + 1).replace('\\', '/'));
                        unchangedListFileOut.write("\n");
                    }
                }
            }
            unchangedListFileOut.close();

            Writer out = new OutputStreamWriter(deleteOut, "utf-8");
            while (oldFileIterator.hasNext()) {
                String oPathStr = oldFileIterator.next().toString();
                File newFile = new File(oPathStr.replace(oldPath, newPath));
                if (!newFile.exists()) {
                    out.write(oPathStr.substring(oldPath.length() + 1).replace('\\', '/'));
                    out.write("\n");
                }
            }
            out.close();
            patchPath = zipFile(outPath, null, null, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return patchPath;
    }
}
