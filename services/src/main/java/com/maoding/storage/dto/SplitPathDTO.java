package com.maoding.storage.dto;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/25 17:25
 * 描    述 :
 */
public class SplitPathDTO {
    /** 路径包含的项目id */
    String projectId; 
    /** 路径包含的项目名 */
    String projectName; 
    /** 路径包含的项目id */
    String issueId; 
    /** 路径包含的签发任务名 */
    String issuePath; 
    /** 路径包含的taskId */
    String taskId; 
    /** 路径包含的生产任务名 */
    String taskPath; 
    /** 路径包含的组织id */
    String companyId; 
    /** 路径包含的组织名 */
    String companyName; 
    /** 路径包含的用户id */
    String userId; 
    /** 路径包含的用户名 */
    String userName; 

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssuePath() {
        return issuePath;
    }

    public void setIssuePath(String issuePath) {
        this.issuePath = issuePath;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskPath() {
        return taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
