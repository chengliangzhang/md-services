package com.maoding.coreBase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maoding.coreUtils.StringUtils;

import javax.persistence.Column;
import java.util.Date;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 19:12
 * 描    述 :
 */
public class BaseEntity extends CoreEntity {
    /** 创建时间 */
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /** 最后修改时间 */
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /** 最后修改者ID */
    @Column
    private String updateBy;

    /** 创建者ID */
    @Column
    private String createBy;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /** 重置删除标志 */
    public void resetDeleted(){}

    /** 重置创建时间为当前时间 */
    public void resetCreateTime() {
        setCreateDate(new Date());
    }

    /** 重置最后更改时间为当前时间 */
    public void resetLastModifyTime() {
        setUpdateDate(new Date());
    }

    /** 兼容设置通用属性 */
    public void setDeleted(Short deleted) {}

    public void setCreateTime(Date createTime) {
        setCreateDate(createTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        setUpdateDate(lastModifyTime);
    }

    public void setLastModifyUserId(String lastModifyUserId) {
        setUpdateBy(lastModifyUserId);
        if (StringUtils.isEmpty(getCreateBy())) {
            setCreateBy(lastModifyUserId);
        }
    }

    public void setLastModifyRoleId(String lastModifyRoleId) {}

    /** 兼容web设置通用属性 */
    public void set4Base(String createBy, String updateBy, Date createDate, Date updateDate){
        setCreateBy(createBy);
        setUpdateBy(updateBy);
        setCreateDate(createDate);
        setUpdateDate(updateDate);
    }

    public void initEntity() {
        reset();
    }

    public void resetCreateDate() {
        resetCreateTime();
    }

    public void resetUpdateDate() {
        resetLastModifyTime();
    }

}

