package com.maoding.coreFileServer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/25 9:55
 * 描    述 :
 */
@Deprecated
public class BasicFileRequestDTO implements Serializable {
    /** 申请文件服务器操作时的访问地址 */
    private String url;
    /** 使用参数的方式，2-通过GET协议，3-通过POST协议，4-通过阿里OSS组件... */
    private Integer mode;
    /** 文件在文件服务器内的存储空间 */
    private String scope;
    /** 文件在文件服务器内的存储名称 */
    private String key;
    /** 申请文件服务器操作时的其他信息 */
    private Map<String,String> params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void putParam(String name, String value){
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name,value);
    }

    public String getParam(String name){
        if ((params == null) || !(params.containsKey(name))) return null;
        return params.get(name);
    }
}
