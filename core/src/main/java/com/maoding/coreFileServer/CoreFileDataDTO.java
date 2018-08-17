package com.maoding.coreFileServer;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/30 18:00
 * 描    述 :
 */
public class CoreFileDataDTO {

    /** 空间(bucket或group) */
    @Deprecated
    private String scope;
    /** 文件id(key或path) */
    @Deprecated
    private String key;
    /** 分片数据在文件内的起始位置，为null则需要根据chunkId和chunkSize计算 */
    private long pos;
    /** 在分片数据数组内有效的字节个数，为null则分片数据数组内所有字节都有效 */
    private int size;
    /** 当前分片数据 */
    private byte[] data;

    @Deprecated
    public String getScope() {
        return scope;
    }

    @Deprecated
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Deprecated
    public String getKey() {
        return key;
    }

    @Deprecated
    public void setKey(String key) {
        this.key = key;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
