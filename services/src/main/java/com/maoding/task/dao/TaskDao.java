package com.maoding.task.dao;

import com.maoding.task.zeroc.QueryTaskDTO;
import com.maoding.task.zeroc.TaskDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/3 15:11
 * 描    述 :
 */
@Repository
public interface TaskDao {
    List<TaskDTO> listTask(QueryTaskDTO query);
}
