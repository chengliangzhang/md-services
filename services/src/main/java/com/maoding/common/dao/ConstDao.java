package com.maoding.common.dao;

import com.maoding.common.entity.ConstEntity;
import com.maoding.common.zeroc.ConstQuery;
import com.maoding.common.zeroc.VersionDTO;
import com.maoding.common.zeroc.VersionQuery;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/20 11:37
 * 描    述 :
 */
@Repository
public interface ConstDao extends Mapper<ConstEntity> {
    /** 获取数据库中的常量 */
    List<ConstEntity> listConst(@NotNull ConstQuery query);
    /** 获取软件版本 */
    List<VersionDTO> listVersion(@NotNull VersionQuery query);
}

