package com.maoding.coreNotice;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 14:41
 * 描    述 :
 */
public class CoreReceiverDTO {
    /** 要发布的频道 */
    private String topic;
    /** 要发送到的项目id */
    @Deprecated
    private String projectId;
    /** 要发送到的组织id */
    @Deprecated
    private String companyId;
    /** 要发送到的用户id */
    private String userId;
    /** 要发送到的用户id列表 */
    private List<String> userIdList;

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
