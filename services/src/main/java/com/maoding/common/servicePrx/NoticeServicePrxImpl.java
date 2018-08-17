package com.maoding.common.servicePrx;

import com.maoding.common.zeroc.CustomException;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.notice.zeroc.*;
import com.maoding.user.zeroc.AccountDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/17 9:50
 * 描    述 :
 */
public class NoticeServicePrxImpl extends CoreRemoteService<NoticeServicePrx> implements NoticeServicePrx{

    private static NoticeServicePrx lastPrx = null;
    private static String lastConfig = null;
    private static NoticeService localService = null;

    private NoticeService getLocalService(){
        if (localService == null) {
            localService = SpringUtils.getBean(NoticeService.class);
        }
        return localService;
    }

    public static NoticeServicePrx getInstance(String config) {
        if ((lastPrx == null) || (StringUtils.isNotSame(lastConfig,config))){
            NoticeServicePrxImpl prx = new NoticeServicePrxImpl();
            lastPrx = prx.getServicePrx(config, NoticeServicePrx.class, _NoticeServicePrxI.class, prx);
            lastConfig = config;
        }
        return lastPrx;
    }

    @Override
    public void subscribeTopicForUser(String id, NoticeClientPrx client) throws CustomException {
        getLocalService().subscribeTopicForUser(id,client,null);
    }

    @Override
    public List<String> listSubscribedTopic(String userId) throws CustomException {
        return getLocalService().listSubscribedTopic(userId,null);
    }

    @Override
    public void sendNotice(NoticeRequestDTO request) throws CustomException {
        getLocalService().sendNotice(request,null);
    }

    @Override
    public void sendNoticeForAccount(AccountDTO account, NoticeRequestDTO request) throws CustomException {
        getLocalService().sendNoticeForAccount(account,request,null);
    }

    @Override
    public CompletableFuture<Void> sendNoticeAsync(NoticeRequestDTO request) {
        new Thread(){
            @Override
            public void run(){
                try {
                    getLocalService().sendNotice(request, null);
                } catch (CustomException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return null;
    }

    @Override
    public CompletableFuture<Void> sendNoticeForAccountAsync(AccountDTO account, NoticeRequestDTO request) {
        new Thread(){
            @Override
            public void run(){
                try {
                    getLocalService().sendNoticeForAccount(account, request, null);
                } catch (CustomException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return null;
    }
}
