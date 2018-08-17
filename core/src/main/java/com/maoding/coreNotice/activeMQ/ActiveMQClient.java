package com.maoding.coreNotice.activeMQ;

import com.maoding.coreNotice.CoreNoticeClient;
import com.maoding.coreNotice.CoreNoticeService;
import com.maoding.coreNotice.CoreReceiverDTO;
import com.maoding.coreUtils.BeanUtils;
import com.maoding.coreUtils.StringUtils;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/18 16:52
 * 描    述 :
 */
@Service("activeMQClient")
public class ActiveMQClient extends AbstractVerticle implements CoreNoticeService{
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** Web 用户消息 **/
    private final static String WEB_USER_MESSAGE = "userMessage";
    private final static String WEB_NOTICE = "notice";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    private ActiveMQMessageDTO getMapMessage(Message msg) throws JMSException {
        Map<?, ?> map = (Map<?, ?>)((MapMessage) msg).getObject("messageEntity");
        log.debug("ActiveMQ Receive:" + map);
        ActiveMQMessageDTO m = new ActiveMQMessageDTO();
        String messageType = (String) map.getOrDefault("messageType", null);
        m.setMessageType(messageType);

        switch (messageType) {
            case WEB_USER_MESSAGE:
                m.setReceiver((String) map.getOrDefault("receiver", null));
                m.setContent((String) map.getOrDefault("msg", null));
                break;
            case WEB_NOTICE:
                m.setContent((String) map.getOrDefault("noticeTitle", null));
                List<String> receiveList = null;
                List<?> list = (List<?>) map.getOrDefault("receiverList", null);
                if (list != null) {
                    for (Object e : list) {
                        if (receiveList == null) receiveList = new ArrayList<>();
                        receiveList.add(e.toString());
                    }
                }
                m.setReceiverList(receiveList);
                break;
        }

        return m;
    }


    private ActiveMQMessageDTO getTextMessage(Message msg) throws JMSException {
        String text = ((TextMessage) msg).getText();
        text = text.replaceAll("\\\\","");
        text = text.replaceAll("\"\\{","{");
        text = text.replaceAll("\\}\"","}");
        text = text.replaceAll(":4",":\"4\"");
        text = text.replaceAll(":0",":\"0\"");
        text = text.replaceAll(":null",":\"\"");
        text = text.replaceAll(":true",":\"true\"");
        text = "{\"id\":\"b164e65f106648d19b4b04e9e3b0751c\",\"queueNo\":2,\"operation\":\"im:account:create\",\"content\":{\"accountId\":\"73486fd0972d40b1b0652364ddb2ba1b\",\"password\":\"E10ADC3949BA59ABBE56E057F20F883E\"},\"fixMode\":\"true\"}";
        log.debug("ActiveMQ Receive:" + text);
        ActiveMQMessageDTO m = new ActiveMQMessageDTO();
        m.setContent(text);
        return m;
    }

    private ActiveMQMessageDTO getMessage(Message msg) throws JMSException{
        if (msg instanceof TextMessage) return getTextMessage(msg);

        else return getMapMessage(msg);
    }

    /**
     * 订阅通告频道
     *
     * @param topic
     * @param handler
     */
    @Override
    public void subscribeTopic(String topic, CoreNoticeClient handler) {
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(topic);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(msg -> {
                try {
                    ActiveMQMessageDTO m = getMessage(msg);
                    com.maoding.coreNotice.CoreMessageDTO coreMessage = new com.maoding.coreNotice.CoreMessageDTO();
                    coreMessage.setContent(m.getContent());
                    handler.notice(coreMessage);
                } catch (JMSException e) {
                    log.error("获取ActiveMQ消息时产生错误", e);
                }
            });
            connection.start();
        } catch (JMSException e) {
            log.error("初始化ActiveMQ客户端错误", e);
        }
    }

    @Override
    public void sendMessage(@NotNull com.maoding.coreNotice.CoreMessageDTO msg, @NotNull CoreReceiverDTO receiver) {
        String topic = receiver.getTopic();

        if (StringUtils.isNotEmpty(topic)) {
            assert (receiver.getUserId() != null) || (receiver.getUserIdList() != null);
            ActiveMQMessageDTO activeMQMessage = BeanUtils.createFrom(msg,ActiveMQMessageDTO.class);
            if (receiver.getUserIdList() != null){
                activeMQMessage.setMessageType(WEB_NOTICE );
                activeMQMessage.setReceiverList(receiver.getUserIdList());
            } else {
                activeMQMessage.setMessageType(WEB_USER_MESSAGE);
                activeMQMessage.setReceiver(receiver.getUserId());
            }
            jmsTemplate.convertAndSend(topic, activeMQMessage);
        }
    }
}
