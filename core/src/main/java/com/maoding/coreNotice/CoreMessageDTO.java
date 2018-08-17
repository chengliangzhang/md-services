package com.maoding.coreNotice;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 11:09
 * 描    述 :
 */
public class CoreMessageDTO {
    /** 发布者id */
    String userId;
    /** 标题 */
    String title;
    /** 内容 */
    String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CoreMessageDTO(){this(null,null,null);}
    public CoreMessageDTO(String userId, String title, String content)
    {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
