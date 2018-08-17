package com.maoding.fileServer.dto;

import com.maoding.coreFileServer.CoreCreateFileRequest;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/26 15:27
 * 描    述 :
 */
public class CopyRequestDTO {
    /** 来源文件服务器类型 */
    private Short srcServerTypeId;
    /** 来源文件服务器地址 */
    private String srcServerAddress;
    /** 目标文件服务器类型 */
    private Short dstServerTypeId;
    /** 目标文件服务器地址 */
    private String dstServerAddress;
    /** 目标文件位置 */
    private String scope;
    /** 目标文件名 */
    private String key;
    /** 文件额外描述信息 */
    private CoreCreateFileRequest extra;

    public Short getSrcServerTypeId() {
        return srcServerTypeId;
    }

    public void setSrcServerTypeId(Short srcServerTypeId) {
        this.srcServerTypeId = srcServerTypeId;
    }

    public String getSrcServerAddress() {
        return srcServerAddress;
    }

    public void setSrcServerAddress(String srcServerAddress) {
        this.srcServerAddress = srcServerAddress;
    }

    public Short getDstServerTypeId() {
        return dstServerTypeId;
    }

    public void setDstServerTypeId(Short dstServerTypeId) {
        this.dstServerTypeId = dstServerTypeId;
    }

    public String getDstServerAddress() {
        return dstServerAddress;
    }

    public void setDstServerAddress(String dstServerAddress) {
        this.dstServerAddress = dstServerAddress;
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

    public CoreCreateFileRequest getExtra() {
        return extra;
    }

    public void setExtra(CoreCreateFileRequest extra) {
        this.extra = extra;
    }
}
