package com.cloud.ops.store;

import com.cloud.ops.utils.CheckMD5;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    @Override
    public String makeFile(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        return filePath;
    }

    @Override
    public String compareWar(String oldWarPath, String newWarPath, String outPath) {
        long startTime = System.currentTimeMillis();  //获取开始时间
        System.out.println("---------------------------------------");
        System.out.println("===> 程序已启动");
        System.out.println("===> 正在解压war包");
        String oldPath = null;
        String newPath = null;
        String patchPath = null;
        try {
            oldPath = unZipFile(oldWarPath, null);
            newPath = unZipFile(newWarPath, null);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        System.out.println("===> 正在遍历文件夹 " + newPath);
        // 遍历新文件目录
        Iterator newFileIterator = FileUtils.iterateFiles(new File(newPath), null, true);
        // 遍历旧文件目录
        Iterator oldFileIterator = FileUtils.iterateFiles(new File(oldPath), null, true);
        // 用于接收被删除的目录
        List<String> deleteFiles = new ArrayList<String>();
        System.out.println("===> 遍历完成，开始执行分析");
        try {

            // 遍历比较新旧目录
            // 1. 如果文件不存在，则说明是新增的文件，复制该文件到输出路径
            // 2. 如果MD5值不一样，则文件被修改，复制该文件到输出路径
            // 3. 如果MD5值一样，则文件未修改，保存mds到files-to-check.txt
            File unchangedListFile = new File(outPath+"/files-to-check.txt");
            if (!unchangedListFile.exists()) {
                createFile(outPath + "/files-to-check.txt");
            }
            Writer unchangedListFileOut = new OutputStreamWriter(new FileOutputStream(unchangedListFile), "utf-8");
            while (newFileIterator.hasNext()) {
                // 新文件路径
                String nPathStr = newFileIterator.next().toString();
                File newFile = new File(nPathStr);
                // 旧文件路径
                File oldFile = new File(nPathStr.replace(newPath, oldPath));
                //System.out.println("===> 正在分析 " + nPathStr);

                // 判断文件是否存在
                if (!oldFile.exists()) {
                    File outFile = new File(nPathStr.replace(newPath, outPath));
                    FileUtils.copyFile(newFile, outFile);
                    System.out.println("======> 新增的文件 " + outFile.toString());
                } else {
                    // MD5校验
                    // 如果不相同，则copy到输出路径
                    String newMD5 = CheckMD5.getMD5(newFile);
                    String oldMD5 = CheckMD5.getMD5(oldFile);
                    if (!StringUtils.equals(newMD5, oldMD5)) {
                        File outFile = new File(nPathStr.replace(newPath, outPath));
                        FileUtils.copyFile(newFile, outFile);
                        System.out.println("======> 覆盖的文件 " + outFile.toString());
                    } else {
                        unchangedListFileOut.write(newMD5 + "  " + nPathStr.substring(newPath.length() + 1).replace('\\', '/'));
                        unchangedListFileOut.write("\n");
                    }
                }
            }
            unchangedListFileOut.close();
            // 遍历旧的文件目录
            // 主要是用于查找被删除的文件
            System.out.println("===> 已找到删除文件 ");
            File deleteListFile = new File(outPath+"/files-to-del.txt");
            if (!deleteListFile.exists()) {
                createFile(outPath + "/files-to-del.txt");
            }
            Writer out = new OutputStreamWriter(new FileOutputStream(deleteListFile), "utf-8");
            while (oldFileIterator.hasNext()) {
                // 旧文件路径
                String oPathStr = oldFileIterator.next().toString();
                // 新文件路径
                File newFile = new File(oPathStr.replace(oldPath, newPath));
                if (!newFile.exists()) {
                    deleteFiles.add(oPathStr);
                    out.write(oPathStr.substring(oldPath.length()+1).replace('\\', '/'));
                    out.write("\n");
                    System.out.println("======> 文件路径 " + oPathStr);
                }
            }
            out.close();
//            genVersionFile(version, outPath);
            patchPath = zipFile(outPath, null, null, true);
        } catch (Exception e) {
            System.err.println("发生异常!");
        }
        long endTime= System.currentTimeMillis(); //获取结束时间
        System.out.println();
        System.out.println("分析完成 耗时：" + ((endTime-startTime) / 1000) + "s");
        System.out.println("---------------------------------------"+patchPath);
        return patchPath;
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            throw new RuntimeException("创建单个文件" + destFileName + "失败，目标文件已存在！");
        }
        if (destFileName.endsWith(File.separator)) {
            throw new RuntimeException("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("创建目标文件所在目录失败！");
            }
        }
        //创建目标文件
        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("创建单个文件" + destFileName + "失败！" + e.getMessage());
        }
    }
    /**
     * 解压包
     * 如果目标路径为空，解压到当前目录，文件夹名同文件名
     *
     * @param zipFilePath
     * @param destPath    目标路径
     * @return
     * @throws ZipException
     */
    public static String unZipFile(String zipFilePath, String destPath) throws ZipException {
        ZipFile zip = new ZipFile(zipFilePath);
        if (destPath == null) {
            destPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
        }
        zip.extractAll(destPath);
        return destPath;
    }
    /**
     * 解压包
     * 如果目标路径为空，解压到当前目录，文件夹名同文件名
     *
     * @param filePath
     * @param destPath 目标路径
     * @return
     * @throws ZipException
     */
    public static String zipFile(String filePath, String zipFileName, String destPath, boolean deleteIfExist) throws ZipException {
        File file = new File(filePath);
        if (file.isFile() && file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()).toLowerCase().equals(".zip")) {
            return filePath;
        }
        if (destPath == null) {
            destPath = file.getParent();
        }
        if (zipFileName == null) {
            if (file.isFile()) {
                zipFileName = file.getName().substring(0, file.getName().lastIndexOf(".")) + ".zip";
            } else {
                zipFileName = file.getName() + ".zip";
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
    public static void main(String[] args) {
        System.out.println(TOPOLOGY_FILE_PATH);
    }

}