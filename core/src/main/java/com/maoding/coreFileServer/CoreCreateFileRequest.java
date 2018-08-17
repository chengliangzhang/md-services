package com.maoding.coreFileServer;

import java.io.File;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/23 16:34
 * 描    述 :
 */
public class CoreCreateFileRequest {
    /** 要创建的文件的节点路径 */
    private String path;

    /** 要创建的文件的长度*/
    private long fileLength;

    /** 要创建的文件的原始文件 */
    private File srcFile;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    public CoreCreateFileRequest(String path,long fileLength,File srcFile){
        setPath(path);
        setFileLength(fileLength);
        setSrcFile(srcFile);
    }
    public CoreCreateFileRequest(String path){
        this(path,0,null);
    }
    public CoreCreateFileRequest(String path,long fileLength){
        this(path,fileLength,null);
    }
    public CoreCreateFileRequest(String path,File srcFile){
        this(path,0,srcFile);
    }
    public CoreCreateFileRequest(File srcFile){
        this(null,0,srcFile);
    }
    public CoreCreateFileRequest(long fileLength){
        this(null,fileLength,null);
    }
    public CoreCreateFileRequest(){
        this(null,0,null);
    }
}
