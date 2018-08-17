package com.maoding.common.config;

import com.maoding.coreUtils.StringUtils;
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
@ConfigurationProperties(prefix = "schedule")
public class ScheduleConfig {
    private final static int DEFAULT_POOL_SIZE = 2;
    private final static boolean DEFAULT_CLEAR_FILE_SERVER_CONFIG = true;
    private final static boolean DEFAULT_CLEAR_BUFFER_CONFIG = true;
    private static final String DEFAULT_UPDATE_CONFIG = "c:/work/maoding-services/update.bat";
    /** 自动清理 */
    private Boolean clearServer;
    /** 自动执行线程个数 */
    private Integer poolSize;
    /** 自动升级 */
    private Boolean autoUpdate;
    /** 升级脚本 */
    private String updateConfig;

    public String getUpdateConfig() {
        return StringUtils.isEmpty(updateConfig) ? DEFAULT_UPDATE_CONFIG : updateConfig;
    }

    public void setUpdateConfig(String updateConfig) {
        this.updateConfig = updateConfig;
    }

    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Integer getPoolSize() {
        return (poolSize != null) ? poolSize : DEFAULT_POOL_SIZE;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public Boolean getClearServer() {
        return (clearServer != null) ? clearServer : DEFAULT_CLEAR_FILE_SERVER_CONFIG;
    }

    public void setClearServer(Boolean clearServer) {
        this.clearServer = clearServer;
    }

}
