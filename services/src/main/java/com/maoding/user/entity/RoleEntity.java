package com.maoding.user.entity;

import com.maoding.coreBase.CoreEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/4/15 8:58
 * 描    述 :
 */
@Table(name = "md_list_role")
public class RoleEntity extends CoreEntity {
    @Column /** 组织编号 */
    private String orgId;
    @Column /** 项目任务编号 */
    private String workId;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
