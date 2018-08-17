package com.maoding.coreBase;

import com.maoding.coreMybatis.CustomMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 19:12
 * 描    述 : 数据库访问层接口（自带通用方法）
 */
public interface CoreDao<T extends CoreEntity> extends Mapper<T>, MySqlMapper<T>, CustomMapper<T> {
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
    default T selectById(@NotNull String id){
        return selectByPrimaryKey(id);
    }

    default int insert(@NotNull T entity){
        return insertSelective(entity);
    }

    default int insertList(@NotNull List<T> entityList){
        int n = 0;
        for (T entity : entityList){
            n += insertSelective(entity);
        }
        return n;
    }

    default int update(@NotNull T entity) {
        return updateById(entity,entity.getId());
    }
    
    default int updateById(@NotNull T entity){
        return updateById(entity, entity.getId());
    }

    default int updateById(@NotNull T entity, @NotNull List<String> idList){
        return updateByIdList(entity, idList);
    }

    default int updateById(@NotNull T entity, @NotNull String id){
        List<String> idList = new ArrayList<>();
        String[] idArray = id.split(",");
        Collections.addAll(idList, idArray);
        return updateByIdList(entity,idList);
    }

    default int updateExact(@NotNull T entity) {
        return updateExactById(entity,entity.getId());
    }

    default int updateExactById(@NotNull T entity) {
        return updateExactById(entity,entity.getId());
    }

    default int updateExactById(@NotNull T entity, @NotNull List<String> idList){
        return updateExactByIdList(entity, idList);
    }

    default int updateExactById(@NotNull T entity, @NotNull String id) {
        List<String> idList = new ArrayList<>();
        String[] idArray = id.split(",");
        Collections.addAll(idList, idArray);
        return updateExactByIdList(entity, idList);
    }

    default int fakeDeleteById(@NotNull String id){
        return fakeDeleteById(id,null,null,null);
    }
    
    default int fakeDeleteById(@NotNull String id, String lastModifyUserId){
        return fakeDeleteById(id,lastModifyUserId,null,null);
    }

    default int fakeDeleteById(@NotNull String id, String lastModifyUserId, String lastModifyRoleId){
        return fakeDeleteById(id,lastModifyUserId,lastModifyRoleId,null);
    }

    default int fakeDeleteById(@NotNull String id, String lastModifyUserId, String lastModifyRoleId, Date lastModifyTime){
        List<String> idList = new ArrayList<>();
        String[] idArray = id.split(",");
        Collections.addAll(idList, idArray);
        return fakeDeleteByIdList(idList,lastModifyUserId,lastModifyRoleId, lastModifyTime);
    }

    default int fakeDeleteById(@NotNull List<String> idList){
        return fakeDeleteByIdList(idList,null,null,null);
    }
    
    default int fakeDeleteById(@NotNull List<String> idList, String lastModifyUserId){
        return fakeDeleteByIdList(idList,lastModifyUserId,null,null);
    }

    default int fakeDeleteById(@NotNull List<String> idList, String lastModifyUserId, String lastModifyRoleId){
        return fakeDeleteByIdList(idList,lastModifyUserId,lastModifyRoleId,null);
    }

    default int fakeDeleteById(@NotNull List<String> idList, String lastModifyUserId, String lastModifyRoleId, Date lastModifyTime){
        return fakeDeleteByIdList(idList,lastModifyUserId,lastModifyRoleId,lastModifyTime);
    }

    default int fakeDeleteByIdList(@NotNull List<String> idList){
        return fakeDeleteByIdList(idList,null,null,null);
    }

    default int fakeDeleteByIdList(@NotNull List<String> idList, String lastModifyUserId){
        return fakeDeleteByIdList(idList,lastModifyUserId,null,null);
    }

    default int fakeDeleteByIdList(@NotNull List<String> idList, String lastModifyUserId, String lastModifyRoleId){
        return fakeDeleteByIdList(idList,lastModifyUserId,lastModifyRoleId,null);
    }
}
