package com.maoding.coreUtils;

import com.maoding.coreBase.CoreDao;
import com.maoding.coreBase.CoreEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/18 11:09
 * 描    述 :
 */
public class EntityUtils {
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(EntityUtils.class);

    public static <T extends CoreEntity> boolean isValid(T entity){
        return (entity != null) && (StringUtils.isNotEmpty(entity.getId()));
    }

    public static String getId(String id, String defaultId){
        return (StringUtils.isNotEmpty(id)) ? id : defaultId;
    }

    public static <T extends CoreEntity> String getId(String id, T entity){
        return (StringUtils.isNotEmpty(id)) ? id : getId(entity);
    }

    public static <T extends CoreEntity> String getId(T entity){
        return (isValid(entity)) ? entity.getId() : null;
    }

    @Deprecated
    public static <T extends CoreEntity> T replace(@NotNull CoreDao<T> dao,@NotNull Class<T> entityClass,Object request,T entity,String id){
        if (ObjectUtils.isEmpty(request)){
            return null;
        }
        if (entity == null){
            entity = BeanUtils.createCleanFrom(request,entityClass);
            if (StringUtils.isNotEmpty(id)){
                entity.setId(id);
            }
            dao.insert(entity);
        } else {
            id = entity.getId();
            BeanUtils.copyProperties(request,entity);

            entity.update();
            dao.update(entity);
        }
        return entity;
    }

    @Deprecated
    public static <T extends CoreEntity> T replace(@NotNull CoreDao<T> dao,@NotNull Class<T> entityClass,Object request,T entity){
        return replace(dao,entityClass,request,entity,null);
    }

    public static <T extends CoreEntity> T replace(@NotNull CoreDao<T> dao,@NotNull Class<T> entityClass,@NotNull Object request,String id){
        T entity = BeanUtils.createCleanFrom(request,entityClass);
        if (StringUtils.isEmpty(id)){
            dao.insert(entity);
        } else {
            entity.setId(id);
            T oldEntity = dao.selectById(id);
            if (oldEntity != null) {
                entity.update();
                dao.update(entity);
                BeanUtils.copyCleanProperties(oldEntity,entity);
                entity = oldEntity;
            } else {
                dao.insert(entity);
            }
        }
        return entity;
    }

    public static <T extends CoreEntity> T replace(@NotNull CoreDao<T> dao,@NotNull Class<T> entityClass,Object request){
        return replace(dao,entityClass,request,(String)null);
    }
}
