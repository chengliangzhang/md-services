package com.maoding.task;

import com.maoding.coreBase.CoreLocalService;
import com.maoding.task.zeroc.QueryTaskDTO;
import com.maoding.task.zeroc.TaskDTO;
import com.maoding.task.zeroc.TaskService;
import com.zeroc.Ice.Current;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/2/7 14:47
 * 描    述 :
 */
@Service("taskService")
public class TaskServiceImpl extends CoreLocalService implements TaskService{
    @Override
    public List<TaskDTO> listTask(QueryTaskDTO query, Current current) {
        return null;
    }
}
