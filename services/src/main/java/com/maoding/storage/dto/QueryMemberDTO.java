package com.maoding.storage.dto;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/22 13:06
 * 描    述 :
 */
public class QueryMemberDTO {
    /** 任务id */
    String taskId;
    /** 要查找的用户角色id */
    Short memberTypeId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Short getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Short memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
}
