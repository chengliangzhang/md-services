package com.maoding.storage.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/8 10:17
 * 描    述 : 子节点查询条件
 */
public class QuerySubNodeDTO {
    /** 用户id列表 */
    List<String> userIdList;
    /** 组织id列表 */
    List<String> orgIdList;
    /** 项目id列表 */
    List<String> projectIdList;
    /** 任务id列表 */
    List<String> taskIdList;
    /** 通告id列表 */
    List<String> noticeIdList;
    /** 报销id列表 */
    List<String> expIdList;
    /** storage_id列表 */
    List<String> storageIdList;
    /** 路径 */
    String parentPath;

    public void addUserId(String userId){
        if (userIdList == null) userIdList = new ArrayList<>();
        userIdList.add(userId);
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public List<String> getOrgIdList() {
        return orgIdList;
    }

    public void setOrgIdList(List<String> orgIdList) {
        this.orgIdList = orgIdList;
    }

    public List<String> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<String> projectIdList) {
        this.projectIdList = projectIdList;
    }

    public List<String> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<String> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public List<String> getNoticeIdList() {
        return noticeIdList;
    }

    public void setNoticeIdList(List<String> noticeIdList) {
        this.noticeIdList = noticeIdList;
    }

    public List<String> getExpIdList() {
        return expIdList;
    }

    public void setExpIdList(List<String> expIdList) {
        this.expIdList = expIdList;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
}
