#pragma once
#include <data/CommonData.ice>
#include <data/UserData.ice>

[["java:package:com.maoding.notice"]]
module zeroc {
    ["java:getset","clr:property"]
    struct MessageDTO { //通告消息
        string userId; //消息发布者id
        string title; //消息标题
        string content; //消息内容
    };
    ["java:type:java.util.ArrayList<MessageDTO>"] sequence<MessageDTO> MessageList;

    ["java:getset","clr:property"]
    struct NoticeRequestDTO { //发送消息申请
        string typeIdString; //用:分隔的消息类型
        string projectId; //要发布到的项目id
        string taskId; //要发布到的任务id
        string companyId; //要发布到的组织id
        string userId; //要发布给的用户id
        StringElementDTO stringElement; //用于替换的字符串
    };
    ["java:type:java.util.ArrayList<NoticeRequestDTO>"] sequence<NoticeRequestDTO> NoticeRequestList;

    ["java:getset","clr:property"]
    struct ReceiverDTO { //接受者
        string topic; //发布频道
        string projectId; //项目id
        string companyId; //组织id
        string userId; //用户id
    };
    ["java:type:java.util.ArrayList<ReceiverDTO>"] sequence<ReceiverDTO> ReceiverList;
};