#pragma once
#include <data/NoticeData.ice>

[["java:package:com.maoding.notice"]]
module zeroc {
    interface NoticeClient {
        void notice(MessageDTO msg); //接收到通知
    };

    interface NoticeService {
        void createTopic(string topic) throws CustomException; //建立主题
        StringList listTopic() throws CustomException; //列出所有已建立的主题
        StringList listSubscribedTopic(string userId) throws CustomException; //列出用户已订阅的主题
        void notice(MessageDTO message,string topic) throws CustomException; //向指定主题发送消息
        void noticeToUser(MessageDTO message,string id) throws CustomException; //向指定用户发送消息
        void noticeToTask(MessageDTO message,string id) throws CustomException; //向指定任务的用户发送消息
        void noticeToProject(MessageDTO message,string id) throws CustomException; //向指定项目的用户发送消息
        void noticeToCompany(MessageDTO message,string id) throws CustomException; //向指定组织的用户发送消息
        void subscribeTopic(string topic, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForAccount(AccountDTO account, NoticeClient* client) throws CustomException; //订阅指定用户的所有频道
        void unSubscribeTopicForAccount(AccountDTO account, NoticeClient* client) throws CustomException; //取消指定用户订阅的所有频道

        void subscribeTopicForWeb(string topic, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForUser(string id, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForTask(string id, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForProject(string id, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForCompany(string id, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForTaskList(StringList idList, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForProjectList(StringList idList, NoticeClient* client) throws CustomException; //订阅频道
        void subscribeTopicForCompanyList(StringList idList, NoticeClient* client) throws CustomException; //订阅频道
        void unSubscribeTopic(string topic, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForUser(string id, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForTask(string id, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForProject(string id, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForCompany(string id, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForTaskList(StringList idList, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForProjectList(StringList idList, NoticeClient* client) throws CustomException; //取消订阅频道
        void unSubscribeTopicForCompanyList(StringList idList, NoticeClient* client) throws CustomException; //取消订阅频道

        void sendNotice(NoticeRequestDTO request) throws CustomException; //发送消息
        void sendNoticeForAccount(AccountDTO account, NoticeRequestDTO request) throws CustomException; //发送消息

    };
};