package com.maoding.coreNotice;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/18 17:54
 * 描    述 :
 */
public interface CoreNoticeClient {
    default void notice(CoreMessageDTO msg){}
}
