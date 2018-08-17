package com.maoding.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/6 17:05
 * 描    述 :
 */
@EnableAutoConfiguration
@Component
@Configuration
@ConfigurationProperties(prefix = "startup")
public class StartupConfig {
    /** 升级数据库命令 */
    private String database;
    /** 启动activemq服务命令 */
    private String activemq;
    /** 启动icegrid服务命令 */
    private String icegrid;
    /** 启动icebox服务命令 */
    private String icebox;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getActivemq() {
        return activemq;
    }

    public void setActivemq(String activemq) {
        this.activemq = activemq;
    }

    public String getIcegrid() {
        return icegrid;
    }

    public void setIcegrid(String icegrid) {
        this.icegrid = icegrid;
    }

    public String getIcebox() {
        return icebox;
    }

    public void setIcebox(String icebox) {
        this.icebox = icebox;
    }
}
