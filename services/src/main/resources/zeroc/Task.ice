#pragma once
#include <data/TaskData.ice>

[["java:package:com.maoding.task"]]
module zeroc {
    interface TaskService {
        TaskList listTask(QueryTaskDTO query) throws CustomException; //查询任务
    };
};