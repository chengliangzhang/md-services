package com.maoding.notice;

import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreNotice.CoreMessageDTO;
import com.maoding.coreNotice.CoreNoticeClient;
import com.maoding.notice.config.NoticeConfig;
import com.maoding.notice.zeroc.MessageDTO;
import com.maoding.notice.zeroc.NoticeClient;
import com.maoding.notice.zeroc.NoticeClientPrx;
import com.zeroc.Ice.*;
import com.zeroc.IceStorm.TopicPrx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/5 23:31
 * 描    述 :
 */
@Service("noticeClient")
public class NoticeClientImpl extends CoreLocalService implements NoticeClient,CoreNoticeClient {


    @Autowired
    NoticeConfig noticeConfig;

    private String userId;
    private TopicPrx topicPrx;

    public NoticeClientImpl(TopicPrx topicPrx){
        this.topicPrx = topicPrx;
    }
    public NoticeClientImpl(String userId){
        this.userId = userId;
    }
    public NoticeClientImpl(){
        this("null");
    }

    @Override
    public void notice(MessageDTO msg, Current current) {
        if (topicPrx != null) {
            ObjectPrx publisher = topicPrx.getPublisher().ice_oneway();
            assert (publisher != null);
            NoticeClientPrx clientPrx = NoticeClientPrx.uncheckedCast(publisher);
            assert (clientPrx != null);
            clientPrx.notice(msg);
        } else {
            log.info(userId + " got messge:" + msg.getTitle() + ":" + msg.getContent() + " from " + msg.getUserId());
        }
    }

    @Override
    public void notice(CoreMessageDTO msg) {
        MessageDTO message = new MessageDTO();
        message.setContent(msg.getContent());
        notice(message,(Current)null);
    }

    public static NoticeClientPrx createNewClient(String locatorIp, String userId){
        String locatorConfig = "IceGrid/Locator:tcp -h " + locatorIp + " -p 4061";
        Communicator communicator = Util.initialize(new String[]{"--Ice.Default.Locator=" + locatorConfig});
        ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("ClientDemo","tcp:udp");
        assert (adapter != null);
        Identity uid = new Identity("userId",userId);
        NoticeClientImpl client = new NoticeClientImpl(userId);
        ObjectPrx proxy = adapter.add(client, uid);
        adapter.activate();
        return NoticeClientPrx.uncheckedCast(proxy);
    }
}
