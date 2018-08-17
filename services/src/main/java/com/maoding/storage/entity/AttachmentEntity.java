package com.maoding.storage.entity;

import com.maoding.coreBase.CoreEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/4/12 21:27
 * 描    述 :
 */
@Table(name = "md_list_attachment")
public class AttachmentEntity extends CoreEntity {
    @Column /** 文件注解编号 */
    private String annotateId;

    @Column /** 文件类附件编号 */
    private String attachmentFileId;

    @Column /** 嵌入元素类附件编号 */
    private String attachmentElementId;

    public String getAnnotateId() {
        return annotateId;
    }

    public void setAnnotateId(String annotateId) {
        this.annotateId = annotateId;
    }

    public String getAttachmentFileId() {
        return attachmentFileId;
    }

    public void setAttachmentFileId(String attachmentFileId) {
        this.attachmentFileId = attachmentFileId;
    }

    public String getAttachmentElementId() {
        return attachmentElementId;
    }

    public void setAttachmentElementId(String attachmentElementId) {
        this.attachmentElementId = attachmentElementId;
    }
}
