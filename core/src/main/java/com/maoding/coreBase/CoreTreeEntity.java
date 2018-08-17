package com.maoding.coreBase;

import javax.persistence.Column;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/20 11:27
 * 描    述 :
 */
public class CoreTreeEntity extends CoreEntity {
    /** 父节点在此表中的id */
    @Column
    private String pid;

    /** 节点路径全名，以"/"作为分隔符 */
    @Column
    private String path;

    /** 细节类型 */
    @Column
    private String typeId;

    /** 树节点名 */
    @Column
    private String nodeName;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
