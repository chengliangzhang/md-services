package com.maoding.coreFileServer.web;

import java.io.Serializable;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/22 17:40
 * 描    述 : 一个POJO。用于保存上传文件的相关信息
 */
public class CoreUploadFileItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // The form field name in a form used foruploading a file,
    // such as "upload1" in "<inputtype="file" name="upload1"/>"
    private String propertyName;

    // File name to be uploaded, thefileName contains path,
    // such as "E:\\some_file.jpg"
    private String fileName;

    public CoreUploadFileItem(String propertyName, String fileName)
    {
        this.propertyName = propertyName;
        this.fileName = fileName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
