package com.maoding.storage.dto;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/21 10:00
 * 描    述 :
 */
public class DesignTaskDTO {
    /** 生产任务id */
    private String id;
    /** 生产任务名字 */
    private String taskName;
    /** 签发任务id */
    private String issueId;
    /** 签发任务名字 */
    private String issueName;
    /** 设计目录路径 */
    private String path;
    /** 任务所属组织id */
    private String companyId;
    /** 任务所属组织名字 */
    private String companyName;
    /** 任务所属项目id */
    private String projectId;
    /** 任务所属项目名字 */
    private String projectName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
