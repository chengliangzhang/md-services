package com.maoding.coreUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/19 10:59
 * 描    述 :
 */
public class ThreadUtils extends Thread {
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(ThreadUtils.class);

    public static final Integer DEFAULT_SLEEP_TIME = 100;

    public static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.debug(e.getMessage());
        }
    }
    public static void sleep(){
        sleep(DEFAULT_SLEEP_TIME);
    }
}
