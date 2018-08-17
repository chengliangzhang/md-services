package com.maoding.mybatis;

import com.maoding.coreBase.CoreShowDTO;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 深圳市卯丁技术有限公司
 * 日期: 2018/8/16
 * 类名: com.maoding.mybatis.TestDTO
 * 作者: 张成亮
 * 描述:
 **/
@Table(name = "md_node_sky_drive")
public class TestDTO extends CoreShowDTO {
    @Column
    private String path;

    @Transient
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
