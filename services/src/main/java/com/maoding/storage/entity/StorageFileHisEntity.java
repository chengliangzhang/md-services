package com.maoding.storage.entity;

import com.maoding.coreBase.CoreEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/28 14:47
 * 描    述 :
 */
@Table(name = "md_list_storage_file_his")
public class StorageFileHisEntity extends CoreEntity {
    @Column /** 协同文件编号id */
    private String mainFileId;
    @Column /** 校审动作类型id */
    private String actionTypeId;
    @Column /** 文件操作时的只读文件长度 */
    private Long fileLength;
    @Column /** 文件操作时的只读文件md5校验值 */
    private String fileMd5;
    @Column /** 操作注解 */
    private String remark;


    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getMainFileId() {
        return mainFileId;
    }

    public void setMainFileId(String mainFileId) {
        this.mainFileId = mainFileId;
    }

    public String getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(String actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
