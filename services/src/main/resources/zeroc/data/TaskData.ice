#pragma once
#include <data/CommonData.ice>

[["java:package:com.maoding.task"]]
module zeroc {
    ["java:getset","clr:property"]
    struct TaskDTO {
        string id; //唯一标识
        string taskName; //任务名称
    };
    ["java:type:java.util.ArrayList<TaskDTO>"] sequence<TaskDTO> TaskList;

    ["java:getset","clr:property"]
    struct QueryTaskDTO {
        string userIdString; //参与者的id，可以是用","分隔的多个id
    };
};