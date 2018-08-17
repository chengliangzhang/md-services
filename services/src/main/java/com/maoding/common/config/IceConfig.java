package com.maoding.common.config;


import com.maoding.coreBase.CoreProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 深圳市卯丁技术有限公司
 * @author  : 张成亮
 * 日    期 : 2017/11/7 18:12
 * 描    述 :
 */
@EnableAutoConfiguration
@Component
@Configuration
@ConfigurationProperties(prefix = "ice")
public class IceConfig extends CoreProperties {
    private final static String ICE_CONFIG_FILE = "--Ice.Config=";
    private final static String DEFAULT_STORAGE = null;
    private final static String DEFAULT_USER = null;
    private final static String DEFAULT_NOTICE = null;
    private final static String DEFAULT_FILE = null;
    private final static String DEFAULT_TOPIC = "IceStorm/TopicManager@StormSvr";

    /** ice服务 */
    private String common;
    private String commonConst;
    private String file;
    private String storage;
    private String user;
    private String notice;
    private String topic;
    /** 本机唯一标记 */
    private String identify;

    public String getCommonConst() {
        return getProperty("commonConst",commonConst);
    }

    public void setCommonConst(String commonConst) {
        this.commonConst = commonConst;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getCommon() {
        return getProperty("common",common);
    }

    public void setCommon(String common) {
        this.common = common;
    }

    public String getFile() {
        return getProperty("file",file);
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTopic() {
        return getProperty("topic",topic);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNotice() {
        return getProperty("notice",notice);
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getStorage() {
        return getProperty("storage",storage);
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getUser() {
        return getProperty("user",user);
    }

    public void setUser(String user) {
        this.user = user;
    }
}
