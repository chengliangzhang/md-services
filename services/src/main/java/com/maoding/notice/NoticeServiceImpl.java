package com.maoding.notice;

import com.maoding.common.LocalConstService;
import com.maoding.common.config.IceConfig;
import com.maoding.common.zeroc.CommonService;
import com.maoding.common.zeroc.CustomException;
import com.maoding.common.zeroc.IdNameDTO;
import com.maoding.common.zeroc.StringElementDTO;
import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreNotice.CoreMessageDTO;
import com.maoding.coreNotice.CoreNoticeClient;
import com.maoding.coreNotice.CoreNoticeService;
import com.maoding.coreNotice.CoreReceiverDTO;
import com.maoding.coreUtils.BeanUtils;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.notice.config.NoticeConfig;
import com.maoding.notice.zeroc.MessageDTO;
import com.maoding.notice.zeroc.NoticeClientPrx;
import com.maoding.notice.zeroc.NoticeRequestDTO;
import com.maoding.notice.zeroc.NoticeService;
import com.maoding.user.zeroc.AccountDTO;
import com.maoding.user.zeroc.QueryMemberDTO;
import com.maoding.user.zeroc.UserJoinDTO;
import com.maoding.user.zeroc.UserServicePrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.IceStorm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 11:31
 * 描    述 :
 */
@Service("noticeService")
public class NoticeServiceImpl extends CoreLocalService implements NoticeService, CoreNoticeClient {

    @Autowired
    IceConfig iceConfig;

    @Autowired
    NoticeConfig noticeConfig;

    private CommonService getCommonService() {
        return SpringUtils.getBean(CommonService.class);
    }

    private UserServicePrx getUserService() throws CustomException {
        return getCommonService().getDefaultUserService(null);
    }

    private CoreNoticeService getCommonNoticeService(){
        return noticeConfig.getCommonNoticeService();
    }

    @Override
    public void subscribeTopicForAccount(@NotNull AccountDTO account, @NotNull NoticeClientPrx client, Current current) throws CustomException {
        //获取用户参与的项目、任务、组织
        UserJoinDTO uj = getUserService().listUserJoinForAccount(account);
        //注册项目、任务、组织频道
        if (uj != null) {
            if (uj.getProjectList() != null){
                for (IdNameDTO idName : uj.getProjectList()){
                    subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + idName.getId(),client,current);
                }
            }
            if (uj.getTaskList() != null){
                for (IdNameDTO idName : uj.getTaskList()){
                    subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + idName.getId(),client,current);
                }
            }
            if (uj.getCompanyList() != null){
                for (IdNameDTO idName : uj.getCompanyList()){
                    subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + idName.getId(),client,current);
                }
            }
        }
        //注册用户频道
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_USER)) + account.getId(),client,current);

        //注册公共频道
        String commonTopicString = noticeConfig.getCommonTopic();
        if (StringUtils.isNotEmpty(commonTopicString)){
            String[] topicArray = commonTopicString.split(";");
            for (String topic : topicArray){
                subscribeTopicForWeb(topic,client,current);
            }
        }
    }

    @Override
    public void unSubscribeTopicForAccount(@NotNull AccountDTO account, @NotNull NoticeClientPrx client, Current current) throws CustomException {
        List<String> topicList = listSubscribedTopic(account.getId(),current);
        if (topicList != null){
            for (String topic : topicList){
                unSubscribeTopic(topic,client,current);
            }
        }
    }

    @Override
    public void subscribeTopicForWeb(String topic, NoticeClientPrx client, Current current) throws CustomException {
        TopicPrx topicPrx = getTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + topic,current);
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + topic,client,current);
        NoticeClientImpl noticeClient = new NoticeClientImpl(topicPrx);
        getCommonNoticeService().subscribeTopic(topic,noticeClient);
    }

    @Override
    public void subscribeTopicForUser(String id, NoticeClientPrx client, Current current) throws CustomException {
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_USER)) + id,client,current);
    }

    @Override
    public void subscribeTopicForTask(String id, NoticeClientPrx client, Current current) throws CustomException {
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + id,client,current);
    }

    @Override
    public void subscribeTopicForProject(String id, NoticeClientPrx client, Current current) throws CustomException {
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + id,client,current);
    }

    @Override
    public void subscribeTopicForCompany(String id, NoticeClientPrx client, Current current) throws CustomException {
        subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + id,client,current);
    }

    @Override
    public void subscribeTopicForTaskList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + id, client, current);
        }
    }

    @Override
    public void subscribeTopicForProjectList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + id, client, current);
        }
    }

    @Override
    public void subscribeTopicForCompanyList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            subscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + id, client, current);
        }
    }

    @Override
    public void unSubscribeTopicForUser(String id, NoticeClientPrx client, Current current) throws CustomException {
        unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_USER)) + id, client, current);
    }

    @Override
    public void unSubscribeTopicForTask(String id, NoticeClientPrx client, Current current) throws CustomException {
        unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + id, client, current);
    }

    @Override
    public void unSubscribeTopicForProject(String id, NoticeClientPrx client, Current current) throws CustomException {
        unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + id, client, current);
    }

    @Override
    public void unSubscribeTopicForCompany(String id, NoticeClientPrx client, Current current) throws CustomException {
        unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + id, client, current);
    }

    @Override
    public void unSubscribeTopicForTaskList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + id, client, current);
        }
    }

    @Override
    public void unSubscribeTopicForProjectList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + id, client, current);
        }
    }

    @Override
    public void unSubscribeTopicForCompanyList(List<String> idList, NoticeClientPrx client, Current current) throws CustomException {
        for (String id : idList) {
            unSubscribeTopic(LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + id, client, current);
        }
    }

    private TopicManagerPrx getTopicManager() throws CustomException {
        return noticeConfig.getTopicManager();
    }

    @Override
    public List<String> listTopic(Current current) throws CustomException {
        List<String> topicList = new ArrayList<>();
        Map<String, TopicPrx> topicMap = getTopicManager().retrieveAll();
        for (Map.Entry<String,TopicPrx> entry : topicMap.entrySet()){
            topicList.add(entry.getKey());
        }
        return topicList;
    }

    @Override
    public List<String> listSubscribedTopic(String userId, Current current) throws CustomException {
        List<String> topicList = new ArrayList<>();
        Map<String, TopicPrx> topicMap = getTopicManager().retrieveAll();
        for (Map.Entry<String,TopicPrx> entry : topicMap.entrySet()){
            TopicPrx topicPrx = entry.getValue();
            Identity[] identities = topicPrx.getSubscribers();
            boolean isSubscribed = false;
            if (identities != null){
                for (Identity id : identities){
                    if (StringUtils.isSame(id.name,"userId") && StringUtils.isSame(id.category,userId)){
                        isSubscribed = true;
                        break;
                    }
                }
            }
            if (isSubscribed) {
                topicList.add(entry.getKey());
            }
        }
        return topicList;
    }

    @Override
    public void noticeToUser(MessageDTO message, String id, Current current) throws CustomException {
        notice(message, LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_USER)) + id,current);
    }

    @Override
    public void noticeToTask(MessageDTO message, String id, Current current) throws CustomException {
        notice(message, LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_TASK)) + id,current);
    }

    @Override
    public void noticeToProject(MessageDTO message, String id, Current current) throws CustomException {
        notice(message, LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_PROJECT)) + id,current);
    }

    @Override
    public void noticeToCompany(MessageDTO message, String id, Current current) throws CustomException {
        notice(message, LocalConstService.getTopicPrefix(Short.toString(LocalConstService.NOTICE_TYPE_COMPANY)) + id,current);
    }

    @Override
    public void notice(MessageDTO message, String topic, Current current) throws CustomException {
        TopicPrx topicPrx = getTopic(topic, current);
        assert (topicPrx != null);
        ObjectPrx publisher = topicPrx.getPublisher().ice_oneway();
        assert (publisher != null);
        NoticeClientPrx clientPrx = NoticeClientPrx.uncheckedCast(publisher);
        assert (clientPrx != null);
        clientPrx.notice(message);
    }

    private boolean isWebNotice(String topic) throws CustomException {
        return (StringUtils.isEmpty(topic)) || topic.startsWith("notify:");
    }

    @Override
    public void subscribeTopic(String topic, NoticeClientPrx client, Current current) throws CustomException {
        final String RETRY_COUNT_KEY = "retryCount";
        final Integer RETRY_COUNT = 1;
        final String RELIABILITY_KEY = "reliability";
        final String RELIABILITY_METHOD = "ordered";
        try {
            Map<String,String> qos = new HashMap<>();
            qos.put(RETRY_COUNT_KEY,RETRY_COUNT.toString());
            qos.put(RELIABILITY_KEY,RELIABILITY_METHOD);
            TopicPrx topicPrx = getTopic(topic,current);
            assert (topicPrx != null);
            for (Identity id : topicPrx.getSubscribers()){
                if (id.equals(client.ice_getIdentity())){
                    topicPrx.unsubscribe(client);
                    break;
                }
            }
            topicPrx.subscribeAndGetPublisher(qos, client);
        } catch (AlreadySubscribed | InvalidSubscriber | BadQoS e) {
            log.warn("无法订阅" + topic + "频道，" + e);
        }
    }

    @Override
    public void unSubscribeTopic(String topic, NoticeClientPrx client, Current current) throws CustomException {
        try {
            TopicPrx topicPrx = getTopicManager().retrieve(topic);
            assert (topicPrx != null);
            topicPrx.unsubscribe(client);
            Identity[] identities = topicPrx.getSubscribers();
            if ((identities == null) || (identities.length == 0)){
                topicPrx.destroy();
            }
        } catch (NoSuchTopic noSuchTopic) {
            log.warn("没有相关频道");
        }
    }

    private TopicPrx getTopic(String topic, Current current) throws CustomException {
        createTopic(topic,current);
        TopicPrx topicPrx = null;
        try {
            topicPrx = getTopicManager().retrieve(topic);
        } catch (NoSuchTopic e) {
            log.warn("没有" + topic + "频道");
        }
        return topicPrx;
    }

    @Override
    public void createTopic(String topic, Current current) throws CustomException {
        try {
            getTopicManager().retrieve(topic);
        } catch(NoSuchTopic e) {
            try {
                getTopicManager().create(topic);
            } catch(TopicExists ex) {
                log.warn("无法创建" + topic + "频道" + e);
            }
        }
    }

    @Override
    public void sendNoticeForAccount(AccountDTO account, NoticeRequestDTO request, Current current) throws CustomException {
        String typeIdString = request.getTypeIdString();
        if (StringUtils.isNotEmpty(typeIdString)) {
            StringElementDTO stringElement = request.getStringElement();
            String[] typeArray = typeIdString.split(":");
            for (String sTypeId : typeArray) {
                String typeId = sTypeId;
                String topic = LocalConstService.getNoticeTopic(typeId);
                if (stringElement != null) {
                    topic = LocalConstService.convertString(topic,stringElement);
                }
                String title = LocalConstService.getNoticeTitle(typeId);
                if (stringElement != null) {
                    title = LocalConstService.convertString(title,stringElement);
                }
                String content = LocalConstService.getNoticeContent(typeId);
                if (stringElement != null) {
                    content = LocalConstService.convertString(content,stringElement);
                }
                String accountId = (account != null) ? account.getId() : null;
                if (!isWebNotice(topic)) {
                    MessageDTO message = new MessageDTO(accountId, title, content);
                    notice(message, topic, current);
                } else {
                    CoreMessageDTO coreMessage = new CoreMessageDTO(accountId, title, content);
                    CoreReceiverDTO coreReceiver = BeanUtils.createCleanFrom(request,CoreReceiverDTO.class);
                    coreReceiver.setTopic(topic);
                    if (StringUtils.isNotEmpty(request.getUserId())){
                        coreReceiver.setUserId(request.getUserId());
                    } else {
                        QueryMemberDTO query = BeanUtils.createCleanFrom(request,QueryMemberDTO.class);
                        List<IdNameDTO> userList = getUserService().listMember(query);
                        if ((userList != null) && (!userList.isEmpty())) {
                            List<String> idList = new ArrayList<>();
                            for (IdNameDTO user : userList){
                                idList.add(user.getId());
                            }
                            coreReceiver.setUserIdList(idList);
                        }
                    }
                    getCommonNoticeService().sendMessage(coreMessage,coreReceiver);
                }
            }
        }
    }

    @Override
    public void sendNotice(@NotNull NoticeRequestDTO request, Current current) throws CustomException {
        sendNoticeForAccount(getUserService().getCurrent(),request,current);
    }

    private String getProjectNameById(String id){
        return id;
    }

    private String getTaskNameById(String id){
        return id;
    }

    private String getCompanyNameById(String id){
        return id;
    }

    private String getUserNameById(String id){
        return id;
    }
}
