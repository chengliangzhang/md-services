package com.maoding.storage.dto;

import com.maoding.storage.entity.StorageTreeEntity;
import com.maoding.storage.entity.StorageFileEntity;
import com.maoding.storage.entity.StorageFileHisEntity;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/18 20:09
 * 描    述 :
 */
public class StorageEntityUnionDTO extends StorageTreeEntity {
    /** 节点对应文件信息 */
    private StorageFileEntity fileEntity;
    /** 节点对应历史操作信息 */
    private StorageFileHisEntity hisEntity;
    /** 节点全路径 */
    private String fullPath;

    public StorageFileEntity getFileEntity() {
        return fileEntity;
    }

    public void setFileEntity(StorageFileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    public StorageFileHisEntity getHisEntity() {
        return hisEntity;
    }

    public void setHisEntity(StorageFileHisEntity hisEntity) {
        this.hisEntity = hisEntity;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
