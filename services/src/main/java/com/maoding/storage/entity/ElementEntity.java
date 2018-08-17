package com.maoding.storage.entity;

import com.maoding.coreBase.CoreEntity;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/3/19 20:04
 * 描    述 :
 */
@Table(name = "md_list_element")
public class ElementEntity extends CoreEntity {
    @Column /** 占位符 */
    private String title;
    @Column /** 元素内容 */
    private byte[] dataArray;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(byte[] dataArray) {
        this.dataArray = dataArray;
    }
}
