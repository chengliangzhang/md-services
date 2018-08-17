package com.maoding.common.config;

import com.maoding.coreBase.CoreProperties;
import com.maoding.coreUtils.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 深圳市卯丁技术有限公司
 *
 * @author : 张成亮
 * 日    期 : 2018/6/13 11:14
 * 描    述 :
 */
@EnableAutoConfiguration
@Component
@Configuration
@ConfigurationProperties(prefix = "project")
public class ProjectConfig extends CoreProperties{
    private static final String DEFAULT_PROJECT_PROPERTIES_CONFIG = "classpath:properties/app.properties";
    private static final String DEFAULT_VERSION = "V1.0";

    private String version;

    public String getVersion() {
        String v = version;
        if (StringUtils.isEmpty(v)) {
            if (StringUtils.isEmpty(getConfig())) {
                setConfig(DEFAULT_PROJECT_PROPERTIES_CONFIG);
            }
            v = getProperty("version",DEFAULT_VERSION);
        }
        return v;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
