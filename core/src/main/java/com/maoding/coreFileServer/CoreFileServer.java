package com.maoding.coreFileServer;

import java.io.File;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/25 9:58
 * 描    述 :
 */
public interface CoreFileServer {
    /** 设定文件服务器存储空间 */
    default void coreSetBaseDir(String baseDir){}
    /** 获取文件服务器存储空间 */
    default String coreGetBaseDir(){return null;}

    /** 设置本地镜像根目录 */
    default void coreSetMirrorBaseDir(String mirrorBaseDir){}
    /** 获取本地镜像根目录 */
    default String coreGetMirrorBaseDir(){return null;}

    /** 设定文件服务器地址 */
    default void coreSetServerAddress(String serverAddress){}
    /** 获取文件服务器地址 */
    default String coreGetServerAddress(){return null;}

    /** 同时设定文件服务器地址和存储空间 */
    default void coreSetServerAddress(String serverAddress,String baseDir){
        coreSetServerAddress(serverAddress,baseDir,null);
    }
    /** 同时设定文件服务器地址,存储空间和本地镜像根目录 */
    default void coreSetServerAddress(String serverAddress,String baseDir,String mirrorBaseDir){
        coreSetServerAddress(serverAddress);
        coreSetBaseDir(baseDir);
        coreSetMirrorBaseDir(mirrorBaseDir);
    }

    /** 设置文件长度 */
    default void coreSetFileLength(String key, long fileLength) {}
    /** 获取文件长度 */
    default long coreGetFileLength(String key) {
        return 0;
    }

    /** 创建文件 */
    default String coreCreateFile(CoreCreateFileRequest createRequest) {return null;}
    default String coreCreateFile(String key, long fileLength) {return coreCreateFile(new CoreCreateFileRequest(key,fileLength));}
    default String coreCreateFile(long fileLength) {return coreCreateFile(new CoreCreateFileRequest(fileLength));}
    default String coreCreateFile(String key, File srcFile) {return coreCreateFile(new CoreCreateFileRequest(key,srcFile));}
    default String coreCreateFile(String key) {return coreCreateFile(new CoreCreateFileRequest(key));}
    default String coreCreateFile(){return coreCreateFile((CoreCreateFileRequest)null);}

    /** 写入文件内容 */
    default int coreWriteFile(CoreFileDataDTO data, String key){return 0;}

    /** 读取文件内容 */
    default CoreFileDataDTO coreReadFile(String key, long pos, int size) {
        return null;
    }
    default CoreFileDataDTO coreReadFile(String key) {
        return coreReadFile(key,0,(int)coreGetFileLength(key));
    }

    /** 计算文件MD5 */
    default String coreCalcMd5(String key){return null;}

    /** 判断在文件服务器上是否存在指定文件 */
    default boolean coreIsExist(String key){return false;}

    /** 移动或重命名文件 */
    default String coreMoveFile(String src, String dst) {return null;}
    
    /** 删除指定文件 */
    default void coreDeleteFile(String key){}

    /** 获取本地文件或镜像文件 */
    default File coreGetFile(String key,String mirrorBaseDir){return null;}
    default File coreGetFile(String key){
        return coreGetFile(key,coreGetMirrorBaseDir());
    }

    /** 清理本地镜像文件 */
    default void coreClearMirrorFile(File mirrorFile){}
    default void coreClearMirrorFile(){}

    /** 获取文件服务器上的文件标识 */
    default List<String> coreListKey(long timeBefore){return null;}
    default List<String> coreListKey(){
        final long DEFAULT_TIME_BEFORE = 3 * 1000;
        return coreListKey(DEFAULT_TIME_BEFORE);
    }
}
