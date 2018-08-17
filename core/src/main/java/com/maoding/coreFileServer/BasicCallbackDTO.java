package com.maoding.coreFileServer;

import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/1 16:27
 * 描    述 :
 */
@Deprecated
public class BasicCallbackDTO {
    /** 回调地址 */
    private String url;
    /** 回调服务器名称 */
    private String name;
    /** 回调参数 */
    private Map<String,String> params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
