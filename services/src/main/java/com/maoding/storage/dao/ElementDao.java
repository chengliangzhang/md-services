package com.maoding.storage.dao;

import com.maoding.common.zeroc.QueryAskDTO;
import com.maoding.storage.zeroc.EmbedElementDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/4/13 12:48
 * 描    述 :
 */
@Repository
public interface ElementDao {
    List<EmbedElementDTO> listElement(QueryAskDTO query);
}
