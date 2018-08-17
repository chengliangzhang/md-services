package com.maoding.storage.dao;

import com.maoding.storage.dto.StorageEntityUnionDTO;
import com.maoding.storage.entity.StorageFileEntity;
import com.maoding.storage.zeroc.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/20 11:37
 * 描    述 :
 */
@Repository
public interface StorageDao {
    @Deprecated
    List<CANodeDTO> listCANode(QueryCANodeDTO query);

    List<NodeFileDTO> listFile(QueryNodeDTO query);
    List<FullNodeDTO> listFullNode(QueryNodeDTO query);
    List<HistoryDTO> listHistory(QueryHistoryDTO query);
    List<SimpleNodeDTO> listWebArchiveDir(QueryNodeDTO query);

    List<SimpleNodeDTO> listNode(QueryNodeDTO query);

    @Deprecated
    List<NodeFileDTO> listNodeFile(QueryNodeFileDTO query);

    StorageEntityUnionDTO selectStorageEntityUnion(QueryNodeDTO query);
    StorageFileEntity selectFileEntity(Map<String,Object> query);

    @Deprecated
    FullNodeDTO getNodeDetailByNodeId(@Param("id") String nodeId, @Param("request") QueryNodeInfoDTO request);

    List<SummaryFileCompanyDTO> summaryFile(QuerySummaryDTO query);
}
