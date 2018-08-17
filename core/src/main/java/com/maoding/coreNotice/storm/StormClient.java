package com.maoding.coreNotice.storm;

import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreNotice.CoreMessageDTO;
import com.maoding.coreNotice.CoreNoticeService;
import com.maoding.coreNotice.CoreReceiverDTO;
import com.maoding.coreUtils.StringUtils;
import com.zeroc.Ice.Communicator;
import com.zeroc.IceStorm.NoSuchTopic;
import com.zeroc.IceStorm.TopicExists;
import com.zeroc.IceStorm.TopicManagerPrx;
import com.zeroc.IceStorm.TopicPrx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 11:16
 * 描    述 :
 */
@Service("stormNotice")
public class StormClient implements CoreNoticeService {
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String DEFAULT_TOPIC_MANAGER_SERVICE = "IceStorm/TopicManager@StormSvr";
    private final static String DEFAULT_COMMUNICATE_CONFIG = "--Ice.Default.Locator=IceGrid/Locator:tcp -h 127.0.0.1 -p 4061";

    private String topicManagerService = null;
    private String communicateConfig = null;

    private static TopicManagerPrx topicManager = null;

    public String getTopicManagerService() {
        return (StringUtils.isNotEmpty(topicManagerService)) ? topicManagerService : DEFAULT_TOPIC_MANAGER_SERVICE;
    }

    public void setTopicManagerService(String topicManagerService) {
        this.topicManagerService = topicManagerService;
    }

    public String getCommunicateConfig() {
        return (StringUtils.isNotEmpty(communicateConfig)) ? communicateConfig : DEFAULT_COMMUNICATE_CONFIG;
    }

    public void setCommunicateConfig(String communicateConfig) {
        this.communicateConfig = communicateConfig;
    }

    public TopicManagerPrx getTopicManager(@NotNull String service, String config) {
        if (topicManager == null) {
            String[] configArray = null;
            if (StringUtils.isNotEmpty(config)){
                configArray = config.split(StringUtils.SPLIT_CONTENT);
            }
            Communicator c = CoreRemoteService.getCommunicator(configArray);
            topicManager = TopicManagerPrx.checkedCast(c.stringToProxy(service));
        }
        return topicManager;
    }

    public TopicManagerPrx getTopicManager(){
        return getTopicManager(getTopicManagerService(), getCommunicateConfig());
    }

    /**
     * 创建通告频道
     *
     * @param topic
     */
    @Override
    public void createTopic(String topic) {
        TopicPrx topicPrx;
        try {
            topicPrx = getTopicManager().retrieve(topic);
        } catch(NoSuchTopic e) {
            try {
                topicPrx = getTopicManager().create(topic);
            } catch(TopicExists ex) {
                log.warn("无法创建" + topic + "频道" + e);
            }
        }
    }

    /**
     * 发布通告
     *
     * @param msg
     * @param receiver
     */
    @Override
    public void sendMessage(CoreMessageDTO msg, CoreReceiverDTO receiver) {

    }
}
