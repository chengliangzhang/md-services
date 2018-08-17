package com.maoding.storage.entity;

import com.maoding.coreBase.CoreTreeEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/3/21 10:08
 * 描    述 :
 */
@Table(name = "md_tree_annotate")
public class AnnotateEntity extends CoreTreeEntity {
    @Column /** 批注评论正文 */
    private String content;
    @Column /** 被批注的文件编号 */
    private String fileId;
    @Column /** 被批注的原始文件编号 */
    private String mainFileId;
    @Column /** 文件批注创建者用户编号 */
    private String creatorUserId;
    @Column /** 文件批注创建者职责编号 */
    private String creatorRoleId;
    @Column /** 文件批注状态编号 */
    private String statusId;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getMainFileId() {
        return mainFileId;
    }

    public void setMainFileId(String mainFileId) {
        this.mainFileId = mainFileId;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getCreatorRoleId() {
        return creatorRoleId;
    }

    public void setCreatorRoleId(String creatorRoleId) {
        this.creatorRoleId = creatorRoleId;
    }
}
