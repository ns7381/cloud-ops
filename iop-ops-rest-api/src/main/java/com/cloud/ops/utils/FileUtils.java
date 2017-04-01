package com.cloud.ops.utils;

import com.google.common.collect.Lists;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoader;

import java.io.*;
import java.util.*;

public class FileUtils {
    private static final String PACKAGE_FILE_PATH = "/iop-ops/package";
    private static final String REPOSITORY_FILE_PATH = "/iop-ops/repository";
    private static final String PATCH_FILE_PATH = "/iop-ops/patch";
    private static final String INTERFACE_FILE_PATH = "/iop-ops/interface";

    public static String getInterfaceFilePath() {
        String uploadPath = ContextLoader.getCurrentWebApplicationContext()
                .getServletContext().getRealPath("/WEB-INF");
        uploadPath = uploadPath.substring(0, uploadPath.indexOf(File.separator))
                + INTERFACE_FILE_PATH + File.separator + UUID.randomUUID();
        File path = new File(uploadPath);
        if (!path.exists()) {// 判断文件夹是否存在
            path.mkdirs();
        }
        return uploadPath;
    }
    public static String getPackageFilePath(String deployId, String packageId) {
        String uploadPath = ContextLoader.getCurrentWebApplicationContext()
                .getServletContext().getRealPath("/WEB-INF");
        uploadPath = uploadPath.substring(0, uploadPath.indexOf(File.separator))
                + PACKAGE_FILE_PATH + File.separator + deployId + File.separator + packageId;
        File path = new File(uploadPath);
        if (!path.exists()) {// 判断文件夹是否存在
            path.mkdirs();
        }
        return uploadPath;
    }

    public static String getRepositoryPath(String appCode) {
        String uploadPath = ContextLoader.getCurrentWebApplicationContext()
                .getServletContext().getRealPath("/WEB-INF");
        uploadPath = uploadPath.substring(0, uploadPath.indexOf(File.separator))
                + REPOSITORY_FILE_PATH + File.separator + appCode + File.separator + UUID.randomUUID();
        File path = new File(uploadPath);
        if (!path.exists()) {// 判断文件夹是否存在
            path.mkdirs();
        }
        return uploadPath;
    }

    public static String getPatchFilePath(String fileName) {
        String uploadPath = ContextLoader.getCurrentWebApplicationContext()
                .getServletContext().getRealPath("/WEB-INF");
        uploadPath = uploadPath.substring(0, uploadPath.indexOf(File.separator))
                + PATCH_FILE_PATH + File.separator + UUID.randomUUID() + File.separator + fileName;
        File path = new File(uploadPath);
        if (!path.exists()) {// 判断文件夹是否存在
            path.mkdirs();
        }
        return uploadPath;
    }

    public static void SaveFileFromInputStream(InputStream stream, String filePath)
            throws IOException {
        FileOutputStream fs = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024 * 1024];
        int byteread = 0;
        while ((byteread = stream.read(buffer)) != -1) {
            fs.write(buffer, 0, byteread);
            fs.flush();
        }
        fs.close();
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

    /**
     * 将文件添加到压缩文件中
     *
     * @param zipFilePath         压缩文件
     * @param addFile             添加的文件
     * @param relativeZipFilePath 添加到zip文件下的相对路径
     */
    public static void addFileToZip(String zipFilePath, File addFile, String relativeZipFilePath) {
        try {
            //解压包
            String extractPath = unZipFile(zipFilePath, null);
            if (relativeZipFilePath == null) {
                relativeZipFilePath = "";
            }
            String copyPath = extractPath + relativeZipFilePath + File.separator + addFile.getName();
            //将需要添加的文件复制到解压文件目标下
            org.apache.commons.io.FileUtils.copyFile(addFile, new File(copyPath));
            File file = new File(zipFilePath);
            file.delete();
            //重新压缩文件
            zipFile(extractPath, file.getName(), null, false);
            //删除解压后的文件
            FileUtils.deleteFile(new File(extractPath));
        } catch (ZipException e) {
            throw new RuntimeException("无法生成带有version的更新包:" + e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 比较new和old的file，取出new中新增和修改file
     *
     * @param newMap
     * @param oldMap
     * @return
     * @throws IOException
     */
    public static Map<String, File> getChangedFiles(Map<String, File> newMap, Map<String, File> oldMap) throws IOException {
        Map<String, File> changedFiles = new HashMap<String, File>();
        for (String key : newMap.keySet()) {
            File newFile = newMap.get(key);
            if (!oldMap.containsKey(key)) {
                changedFiles.put(key, newFile);
                continue;
            }
            File oldFile = oldMap.get(key);
            if (!org.apache.commons.io.FileUtils.contentEquals(newFile, oldFile)) {
                changedFiles.put(key, newFile);
            }
        }
        return changedFiles;
    }

    /**
     * 获取的target中没有的source的key的数据
     *
     * @param source
     * @param target
     * @return
     */
    public static Map<String, File> getUnContain(Map<String, File> source, Map<String, File> target) {
        Map<String, File> unContain = new HashMap<String, File>();
        for (String key : source.keySet()) {
            if (!target.containsKey(key)) {
                unContain.put(key, source.get(key));
            }
        }
        return unContain;
    }

    /**
     * 根据父路径，将file列表组装为map，key为相对父路径的相对路径
     *
     * @param parent
     * @return
     */
    public static Map<String, File> findFilesMap(File parent) {
        List<File> newFiles = findFiles(parent);
        return getFilesMap(newFiles, parent);
    }

    /**
     * 根据父路径，将file列表组装为map，key为相对父路径的相对路径
     *
     * @param fileList
     * @param parent
     * @return
     */
    private static Map<String, File> getFilesMap(List<File> fileList, File parent) {
        Map<String, File> filesMap = new HashMap<String, File>();
        String parentPath = parent.getAbsolutePath();
        for (File file : fileList) {
            filesMap.put(file.getAbsolutePath().replace(parentPath, ""), file);
        }
        return filesMap;
    }

    /**
     * 获递归获取目录下指定数量的文件。在读取CDA文件PACS文件时会调用。
     *
     * @param dir File 目标目录
     * @return 读取的文件列表
     */
    private static List<File> findFiles(File dir) {
        List<File> fileList = Lists.newArrayList();
        // 文件目录不为空
        if (dir == null || !dir.isDirectory()) {
            return fileList;
        }
        fileList.addAll(findFile(dir));
        List<File> directorylist = findDirectoy(dir);

        // 遍历获取定量文件
        for (int i = 0; i < directorylist.size(); i++) {
            fileList.addAll(findFiles((File) directorylist.get(i)));
        }
        return fileList;
    }

    /**
     * 获取文件对象
     *
     * @param dir 目标目录
     * @return 文件列表
     */
    public static List<File> findFile(File dir) {
        List<File> fileList = Lists.newArrayList();
        // 如果目标目录不为空
        if (dir == null || !dir.isDirectory()) {
            return fileList;
        }
        File[] files = dir.listFiles();
        // 遍历添加文件到返回值
        for (int i = 0; i < files.length; i++) {
            // 如果是文件不是目录
            if (files[i].isFile()) {
                fileList.add(files[i]);
            }
        }
        return fileList;
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
     * 获取目录列表
     *
     * @param dir 目标目录
     * @return 目录列表list
     */
    private static List<File> findDirectoy(File dir) {
        List<File> fileList = Lists.newArrayList();
        // 如果目标不为空
        if (dir == null || !dir.isDirectory()) {
            return fileList;
        }
        File[] files = dir.listFiles();
        // 遍历为返回值添加目录文件
        for (int i = 0; i < files.length; i++) {
            // 如果是目录
            if (files[i].isDirectory()) {
                fileList.add(files[i]);
            }
        }
        return fileList;
    }

    /**
     * 级联删除文件或文件夹全部内容
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                for (File child : file.listFiles()) { // 遍历目录下所有的文件
                    deleteFile(child); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

    public static String compareWar(String oldWarPath, String newWarPath, String outPath, String version) {
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
        Iterator newFileIterator = org.apache.commons.io.FileUtils.iterateFiles(new File(newPath), null, true);
        // 遍历旧文件目录
        Iterator oldFileIterator = org.apache.commons.io.FileUtils.iterateFiles(new File(oldPath), null, true);
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
                FileUtils.createFile(outPath + "/files-to-check.txt");
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
                    org.apache.commons.io.FileUtils.copyFile(newFile, outFile);
                    System.out.println("======> 新增的文件 " + outFile.toString());
                } else {
                    // MD5校验
                    // 如果不相同，则copy到输出路径
                    String newMD5 = CheckMD5.getMD5(newFile);
                    String oldMD5 = CheckMD5.getMD5(oldFile);
                    if (!StringUtils.equals(newMD5, oldMD5)) {
                        File outFile = new File(nPathStr.replace(newPath, outPath));
                        org.apache.commons.io.FileUtils.copyFile(newFile, outFile);
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
                FileUtils.createFile(outPath + "/files-to-del.txt");
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
            genVersionFile(version, outPath);
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
    /**
     * 生成新的version文件
     * @param version
     * @return
     */
    private static File genVersionFile(String version, String filePath) {
        String path = "version";
        if(filePath != null){
            File pathFile = new File(filePath);
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }
            path = filePath + File.separator + "version";
        }
        File versionFile = new File(path);
        FileWriter fw;
        try {
            fw = new FileWriter(versionFile);
            fw.write("version=" + version);
            fw.write("\r\n");
            fw.write("date=" + new Date());
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException("无法生成version文件:"+e.getMessage());
        }
        return versionFile;
    }
    public static void main(String[] args) {
        compareWar("E:\\update-version\\wars\\cloud-iopm\\init\\cloud-web-3.1.0-SNAPSHOT.war",
                "E:\\update-version\\wars\\cloud-iopm\\update\\cloud-web-3.1.0-SNAPSHOT.war",
                "E:\\update-version\\wars\\cloud-iopm\\update\\test","2.0");
    }

}