package com.maoding.coreUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/6 20:57
 * 描    述 :
 */
public class FileUtils {
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private static final int DEFAULT_BUFFER_SIZE = 2048 * 1024;

    public static void ensureDirExist(String dir){
        if (StringUtils.isNotEmpty(dir)) {
            File fd = new File(StringUtils.formatPath(dir));
            if (!fd.exists()) {
                boolean isSuccess = fd.mkdirs();
                assert (isSuccess);
            } else if (!fd.isDirectory()) {
                log.error("存在同名文件");
            }
        }
    }

    public static void ensurePathExist(String path){
        ensureDirExist(StringUtils.getDirName(path));
    }

    public static void close(Closeable handle) {
        if (handle != null) {
            try {
                handle.close();
            } catch (IOException e) {
                log.error("关闭文件出错",e);
            }
        }
    }

    public static void setFileLength(@NotNull File file, long fileLength){
        assert (file.isFile());

        RandomAccessFile rf = null;
        try {
            log.info("打开文件设置长度为" + fileLength + "：" + file.getPath());
            rf = new RandomAccessFile(file, "rw");
            rf.setLength(fileLength);
        } catch (IOException e) {
            log.error("设置文件" + file.getPath() + "长度时出错", e);
        } finally {
            FileUtils.close(rf);
            log.info("关闭设置长度的文件：" + file.getPath());
        }
    }

    public static void copyFile(@NotNull File srcFile, @NotNull File dstFile){
        assert (isValidFile(srcFile) && dstFile.isFile());

        //复制文件
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            log.info("打开文件进行复制：源文件：" + srcFile.getPath() + ",目标文件：" + dstFile.getPath());
            inStream = new FileInputStream(srcFile);
            outStream = new FileOutputStream(dstFile);
            FileChannel in = inStream.getChannel();
            FileChannel out = outStream.getChannel();
            while (in.position() < in.size()) {
                int length = DEFAULT_BUFFER_SIZE;
                if ((in.size() - in.position()) < length) {
                    length = (int) (in.size() - in.position());
                }
                ByteBuffer buf = ByteBuffer.allocateDirect(length);
                in.read(buf);
                buf.flip();
                out.write(buf);
            }
        } catch (IOException e) {
            log.error("从" + srcFile.getPath() + "复制到" + dstFile.getPath() + "时出错", e);
        } finally {
            close(outStream);
            close(inStream);
            log.info("关闭复制的文件：" + srcFile.getPath() + "," + dstFile.getPath());
        }
    }

    public static boolean isValidFile(File file){
        return (file != null) && file.exists() && file.isFile();
    }

    public static String calcMd5(@NotNull File srcFile) {
        assert (isValidFile(srcFile));

        String md5 = null;
        FileInputStream inStream = null;
        try {
            log.info("打开文件计算Md5：" + srcFile.getPath());
            inStream = new FileInputStream(srcFile);
            md5 = DigestUtils.md5Hex(inStream).toUpperCase();
        } catch (IOException e) {
            log.error("读取文件" + srcFile.getPath() + "时出错",e);
        } finally {
            close(inStream);
            log.info("关闭计算Md5的文件：" + srcFile.getPath());
        }
        return md5;
    }

    public static long getFileLength(@NotNull File file) {
        assert (isValidFile(file));
        return file.length();
    }

    public static String getFileLengthStr(@NotNull File file) {
        assert (isValidFile(file));
        return Long.toString(getFileLength(file));
    }

    public static int writeDataToFile(@NotNull File file, long pos, int size, byte[] data) {
        assert (!file.exists() || file.isFile());
        assert ((pos >= 0) && (size > 0) && (data != null) && (size <= data.length));

        ensurePathExist(file.getPath());

        //打开文件
        RandomAccessFile rf = null;
        try {
            log.info("打开文件计算写入数据：" + file.getPath());
            rf = new RandomAccessFile(file, "rw");
            if (rf.length() < (pos + size)) {
                rf.setLength(pos + size);
            }
            rf.seek(pos);
            rf.write(data, 0, size);
        } catch (IOException e) {
            log.error("写入文件" + file.getPath() + "时出错");
            size = 0;
        } finally {
            close(rf);
            log.info("关闭写入数据的文件：" + file.getPath());
        }

        return size;
    }

    public static void executeCmd(@NotNull String[] cmdArray){
        if (cmdArray.length > 0) {
            try {
                Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
                log.error("执行命令时出现错误",e);
            }
        }
    }

    public static void executeCmd(@NotNull String cmd, String title) {
        String[] cmdArray = analysisCmd(cmd,title);
        executeCmd(cmdArray);
    }

    public static void executeCmd(@NotNull String cmd) {
        executeCmd(cmd,null);
    }

    public static String[] analysisCmd(@NotNull String[] cmdArray,String title){
        final String batExt = ".bat";
        final String cmdCommand = "cmd";
        final String cmdStartParamName = "/c";
        final String cmdStartParamValue = "start";

        if ((cmdArray.length > 0) && (StringUtils.isSame(StringUtils.getFileExt(cmdArray[0]),batExt))){
            List<String> cmdList = new ArrayList<>();
            cmdList.add(cmdCommand);
            cmdList.add(cmdStartParamName);
            cmdList.add(cmdStartParamValue);
            if (StringUtils.isNotEmpty(title)) {
                cmdList.add("\"" + title + "\"");
            } else {
                cmdList.add("\"" + cmdArray[0] + "\"");
            }
            Collections.addAll(cmdList, cmdArray);
            cmdArray = cmdList.toArray(new String[cmdList.size()]);
        }
        return cmdArray;
    }

    public static String[] analysisCmd(@NotNull String cmd,String title){
        final String cmdSplit = StringUtils.SPLIT_CONTENT;
        String[] cmdArray = cmd.split(cmdSplit);
        return analysisCmd(cmdArray,title);
    }

    public static String[] analysisCmd(@NotNull String cmd){
        return analysisCmd(cmd,null);
    }

}
