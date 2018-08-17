package com.maoding.coreNotice;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 9:39
 * 描    述 :
 */
public interface CoreNoticeService {
    final static Short NOTICE_SERVER_TYPE_STORM = 0;

    /** 创建通告频道 */
    default void createTopic(String topic){}
    /** 订阅通告频道 */
    default void subscribeTopic(String topic, CoreNoticeClient handler){}
    /** 取消订阅 */
    default void unSubscribeTopic(String topic, CoreNoticeClient handler){}
    /** 发布通告 */
    default void sendMessage(CoreMessageDTO msg, CoreReceiverDTO receiver){}
}
