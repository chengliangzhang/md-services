package com.maoding.storage.dao;

import com.maoding.coreBase.CoreDao;
import com.maoding.storage.entity.StorageTreeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/3/10 15:08
 * 描    述 :
 */
@Repository
public interface StorageTreeDao extends CoreDao<StorageTreeEntity> {
    StorageTreeEntity selectByTaskIdAndFuzzyPath(@Param("taskId") String taskId, @Param("fuzzyPath") String fuzzyPath);
    int updateTaskIdByPid(@Param("pid") String pid,@Param("taskId") String taskId);
}
