package com.maoding.coreUtils;

import org.slf4j.Logger;

/**
 * 深圳市卯丁技术有限公司
 *
 * @author : 张成亮
 * 日    期 : 2018/6/7 18:14
 * 描    述 :
 */
public class TraceUtils {
    public static long enter(Logger log,Object... obs){
        log.info("\t===>>> 进入" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + getJsonString(obs));
        return System.currentTimeMillis();
    }

    @Deprecated
    public static long enter(Logger log,String func,Object... obs){
        return enter(log,obs);
    }

    public static void exit(Logger log,long t,Object... obs){
        log.info("\t<<<=== 退出" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + (System.currentTimeMillis() - t) + "ms," + getJsonString(obs));
    }

    @Deprecated
    public static void exit(Logger log,String func,long t,Object... obs){
        log.info("\t<<<=== 退出" + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + (System.currentTimeMillis() - t) + "ms," + getJsonString(obs));
    }

    public static long info(Logger log,String message,long t,Object... obs){
        log.info("\t===>>> " + message + ":" + (System.currentTimeMillis() - t) + "ms," + getJsonString(obs));
        return System.currentTimeMillis();
    }

    public static void check(Logger log, boolean condition, Class<? extends RuntimeException> eClass) {
        if (!(condition)) {
            if (eClass != null) {
                RuntimeException e = null;
                try {
                    e = eClass.newInstance();
                    log.error("\t!!!>>> " + Thread.currentThread().getStackTrace()[2].getMethodName() + ":" + e.getMessage());
                } catch (InstantiationException | IllegalAccessException ex) {
                    log.error("\t!!!!! " + ex.getMessage());
                }
                if (e != null) {
                    throw e;
                }
            } else {
                log.warn("\t!!!>>> " + Thread.currentThread().getStackTrace()[2].getMethodName() + "存在错误");
            }
        }
    }

    private static String getJsonString(Object... obs){
        if (obs != null) {
            StringBuilder s = new StringBuilder();
            for (Object o : obs) {
                s.append(JsonUtils.obj2CleanJson(o));
            }
            return s.toString();
        } else {
            return "";
        }
    }
}
