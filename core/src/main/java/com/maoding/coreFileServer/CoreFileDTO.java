package com.maoding.coreFileServer;

import com.maoding.coreUtils.StringUtils;

import java.io.Serializable;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/25 9:53
 * 描    述 :
 */
@Deprecated
public class CoreFileDTO implements Serializable {
    /** 服务器地址 */
    @Deprecated
    private String serverAddress;
    /** 文件空间，即阿里云的buchet或FastFDS的group */
    private String scope;
    /** 文件标志，即阿里云的key或FastFDS的key */
    private String key;
    /** 文件长度 */
    private Long length;
    /** 文件校验和 */
    private String checksum;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public CoreFileDTO(String serverAddress, String scope, String key){
        this.serverAddress = serverAddress;
        this.scope = scope;
        this.key = key;
    }
    public CoreFileDTO(String scope, String key){
        this.scope = scope;
        this.key = key;
    }
    public CoreFileDTO(String path){
        this(StringUtils.getDirName(path),StringUtils.getFileName(path));
    }
    public CoreFileDTO(){
        this(null,null);
    }
}
