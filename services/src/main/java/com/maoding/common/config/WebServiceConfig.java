package com.maoding.common.config;

import com.maoding.coreBase.CoreProperties;
import com.maoding.coreUtils.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/13 15:17
 * 描    述 :
 */
@Component
@ConfigurationProperties(prefix = "webService")
public class WebServiceConfig extends CoreProperties {
    private final static String DEFAULT_WEB_URL = "http://172.16.6.73/maoding";
    private final static String DEFAULT_FILE_CENTER_URL = "http://172.16.6.71:8071";

    /** 卯丁web服务根地址 */
    private String maodingWeb;
    /** file-center服务根地址 */
    private String fileCenter;
    private CloseableHttpClient client = null;

    public String getMaodingWeb() {
        return getProperty("maodingWeb",(StringUtils.isEmpty(maodingWeb) ? DEFAULT_WEB_URL : maodingWeb));
    }

    public void setMaodingWeb(String maodingWeb) {
        this.maodingWeb = maodingWeb;
    }

    public String getFileCenter() {
        return getProperty("fileCenter",(StringUtils.isEmpty(fileCenter) ? DEFAULT_FILE_CENTER_URL : fileCenter));
    }

    public void setFileCenter(String fileCenter) {
        this.fileCenter = fileCenter;
    }

    public String getLoginUrl() {
        String LOGIN_URL = "/iWork/sys/login";
        return getMaodingWeb() + LOGIN_URL;
    }

    public String getLoginParamsType() {
        return "application/json";
    }

    public String getGetCurrentUrl() {
        String GET_WORK_URL = "/iWork/sys/getCurrUserOfWork";
        return getMaodingWeb() + GET_WORK_URL;
    }

    public String getLoadProjectDetailsUrl(){
        return getMaodingWeb() + "/iWork/project/loadProjectDetails";
    }

    public String getUploadUrl(){
        return getMaodingWeb() + "/netFile/uploadFile";
    }

    public CloseableHttpClient getClient() {
        if (client == null){
            client = HttpClients.createDefault();
        }
        return client;
    }

    public void setClient(CloseableHttpClient client) {
        this.client = client;
    }

    public String getGetCurrentIdKey() {
        return "id";
    }

    public String getGetCurrentNameKey() {
        return "userName";
    }

    public String getGetCurrentInfoKey() {
        return "userInfo";
    }

    public String getUpload(){
        return "netFile/uploadFile";
    }
}
