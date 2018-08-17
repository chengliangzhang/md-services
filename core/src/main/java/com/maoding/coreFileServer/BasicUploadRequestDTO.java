package com.maoding.coreFileServer;

import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/30 19:00
 * 描    述 :
 */
@Deprecated
public class BasicUploadRequestDTO {
    /** 上传申请的唯一编号 */
    private Integer requestId;
    /** 上传任务ID */
    private String uploadId;
    /** 总分片数量 */
    private Integer chunkCount;
    /** 每个分片的约定大小 */
    private Integer chunkPerSize;
    /** 要上传的分片序号 */
    private Integer chunkId;
    /** 要上传的分片大小 */
    private Integer chunkSize;
    /** 要上传的文件数据 */
    private CoreFileDataDTO multipart;
    /** 其他上传参数 */
    private Map<String,String> params;

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(Integer chunkCount) {
        this.chunkCount = chunkCount;
    }

    public Integer getChunkPerSize() {
        return chunkPerSize;
    }

    public void setChunkPerSize(Integer chunkPerSize) {
        this.chunkPerSize = chunkPerSize;
    }

    public Integer getChunkId() {
        return chunkId;
    }

    public void setChunkId(Integer chunkId) {
        this.chunkId = chunkId;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public CoreFileDataDTO getMultipart() {
        return multipart;
    }

    public void setMultipart(CoreFileDataDTO multipart) {
        this.multipart = multipart;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
