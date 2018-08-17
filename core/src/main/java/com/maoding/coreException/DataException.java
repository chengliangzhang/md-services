package com.maoding.coreException;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 19:12
 * 描    述 : 数据库内数据异常
 */
public final class DataException extends RuntimeException {

    public DataException(String message) {
        super(message);
    }
}
