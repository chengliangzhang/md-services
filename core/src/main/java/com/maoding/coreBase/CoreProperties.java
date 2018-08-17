package com.maoding.coreBase;

import com.maoding.coreUtils.DigitUtils;
import com.maoding.coreUtils.ObjectUtils;
import com.maoding.coreUtils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/6 14:14
 * 描    述 :
 */
public class CoreProperties {
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String DEFAULT_CODE = "ISO-8859-1";
    private final static String DEFAULT_CHARSET = "utf-8";

    private Map<String, String> propertiesMap;
    private long lastRefreshTime = 0;

    /** 配置文件 */
    private String config;
    /** 配置文件刷新时间 */
    private Long refreshTime;

    public Long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    private void loadAllProperties(@NotNull Properties props) throws BeansException {
        propertiesMap = new HashMap<>(props.size());
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            try {
                propertiesMap.put(keyStr, new String(props.getProperty(keyStr).getBytes(DEFAULT_CODE), DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                log.error("加载配置属性出错",e);
            }
        }
        lastRefreshTime = System.currentTimeMillis();
    }

    private void loadAllProperties(@NotNull String config) {
        final String classPathString = "classpath:";
        try {
            if (StringUtils.startsWith(config,classPathString)) {
                ClassLoader classLoader = getClass().getClassLoader();
                URL url = classLoader.getResource(config);
                if (url != null) {
                    config = url.getFile();
                }
            }
            ResourceLoader loader = new FileSystemResourceLoader();
            Resource resource = loader.getResource(config);
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            if (ObjectUtils.isNotEmpty(properties)) {
                loadAllProperties(properties);
            }
        } catch (IOException e) {
            log.error("初始化配置文件出错",e);
        }
    }

    private boolean needReload() {
        return (lastRefreshTime <= 0) ||
                ((getRefreshTime() != null) &&
                        ((System.currentTimeMillis() - lastRefreshTime) > DigitUtils.parseLong(getRefreshTime())));
    }
    private void refresh() {
        if (needReload()) {
            if (StringUtils.isNotEmpty(getConfig())) {
                loadAllProperties(getConfig());
            }
        }
    }

    public String getProperty(@NotNull String propertyName){
        return getProperty(propertyName,null);
    }

    public String getProperty(String propertyName,String defaultValue){
        refresh();
        String value = defaultValue;
        if (propertiesMap != null) {
            for (Map.Entry<String,String> entry : propertiesMap.entrySet()){
                if (StringUtils.isSame(propertyName,entry.getKey())){
                    value = entry.getValue();
                    break;
                }
            }
        }
        return value;
    }

}
