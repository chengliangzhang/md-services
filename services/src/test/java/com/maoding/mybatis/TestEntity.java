package com.maoding.mybatis;

import com.maoding.coreBase.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 深圳市卯丁技术有限公司
 * 日期: 2018/8/16
 * 类名: com.maoding.mybatis.provider.TestDTO
 * 作者: 张成亮
 * 描述:
 **/
@Table(name = "maoding_web_project_sky_drive")
public class TestEntity extends BaseEntity {
    @Transient
    private String name;

    @Column
    private String fileName;
    @Column
    private String filePath;

    @Transient
    private String path;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
