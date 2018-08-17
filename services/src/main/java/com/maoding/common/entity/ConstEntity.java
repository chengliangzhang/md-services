package com.maoding.common.entity;

import com.maoding.coreBase.CoreEntity;
import com.maoding.coreUtils.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市设计同道技术有限公司
 * 类    名 : ConstEntity
 * 描    述 :
 * @author : 张成亮
 * 日    期 : 2018/6/7 14:40
 */
@Table(name = "md_list_const_custom")
public class ConstEntity extends CoreEntity {
    /** 分类id */
    @Column
    private Short classicId;
    /** 值id */
    @Column
    private String codeId;
    /** 显示信息 */
    private String title;
    /** 控制定义 */
    private String extra;

    public Short getClassicId() {
        return classicId;
    }

    public void setClassicId(Short classicId) {
        this.classicId = classicId;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle(int n) {
        return StringUtils.getContent(title,n);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtra() {
        return extra;
    }

    public String getExtra(int n) {
        return StringUtils.getContent(extra,n);
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
