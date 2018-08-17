package com.maoding.storage.dto;

import java.util.Date;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/8 17:58
 * 描    述 :
 */
public class QueryHistoryDTO {
    /** 历史记录id */
    private String id;
    /** 原始文件id */
    private String fileId;
    /** 操作类型id */
    private Short actionTypeId;
    /** 最晚操作时间 */
    private Date beforeModifyTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Short getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(Short actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public Date getBeforeModifyTime() {
        return beforeModifyTime;
    }

    public void setBeforeModifyTime(Date beforeModifyTime) {
        this.beforeModifyTime = beforeModifyTime;
    }
}
