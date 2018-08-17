package com.maoding.coreFileServer.web;

import java.io.Serializable;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/22 17:39
 * 描    述 : 一个POJO。用于处理普通表单域形如key = value对的数据
 */
public class CoreKeyValuePair implements Serializable {
    private static final long serialVersionUID = 1L;

    // The form field used for receivinguser's input,
    // such as "username" in "<inputtype="text" name="username"/>"
    private String key;
    // The value entered by user in thecorresponding form field,
    // such as "Patrick" the abovementioned formfield "username"
    private String value;

    public CoreKeyValuePair(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
