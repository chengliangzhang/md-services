package com.maoding.storage.entity;

import com.maoding.coreBase.CoreTreeEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/17 10:55
 * 描    述 :
 */
@Table(name = "md_tree_storage")
public class StorageTreeEntity extends CoreTreeEntity {
    @Column /** 相关联的任务id */
    private String taskId;
    @Column /** 相关联的项目id */
    private String projectId;
    @Column /** 文件所属用户id */
    private String ownerUserId;
    @Column /** 节点文件长度，与只读文件的长度相同 */
    private Long fileLength;
    @Column /** 节点文件md5校验值，与只读文件md5校验值相同 */
    private String fileMd5;

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
