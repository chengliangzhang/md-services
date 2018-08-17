#pragma once
#include <data/StorageData.ice>

[["java:package:com.maoding.storage"]]
module zeroc {
    interface StorageService {
        NodeFileList listFile(QueryNodeDTO query) throws CustomException; //列出文件节点
        FullNodeList listFullNode(QueryNodeDTO query) throws CustomException; //列出文件节点
        HistoryList listHistory(QueryHistoryDTO query) throws CustomException; //列出历史动作节点

        ["deprecate"] CANodeList listCANode(QueryCANodeDTO query) throws CustomException; //列出设计及校验审核文档

        EmbedElementDTO createEmbedElement(UpdateElementDTO request) throws CustomException; //创建内嵌HTML元素
        EmbedElementDTO updateEmbedElement(EmbedElementDTO src,UpdateElementDTO request) throws CustomException; //创建内嵌HTML元素
        EmbedElementList listEmbedElement(QueryAskDTO query) throws CustomException; //创建内嵌HTML元素

        AnnotateDTO createAnnotate(NodeFileDTO file,UpdateAnnotateDTO request) throws CustomException; //创建文件注解
        AnnotateDTO updateAnnotate(AnnotateDTO src,UpdateAnnotateDTO request) throws CustomException; //更新文件注解
        AnnotateList listAnnotate(QueryAnnotateDTO query) throws CustomException; //查询文件注解

        NodeFileDTO createFile(UpdateNodeFileDTO request) throws CustomException; //创建文件
        NodeFileDTO createFileWithId(UpdateNodeFileDTO request,string id) throws CustomException; //创建文件
        NodeFileDTO createMirror(NodeFileDTO src,UpdateNodeFileDTO request) throws CustomException; //创建镜像
        NodeFileDTO updateFile(NodeFileDTO src,UpdateNodeFileDTO request) throws CustomException; //更新文件或镜像

        ["deprecate"] NodeFileDTO createNodeFile(NodeFileDTO src,UpdateNodeFileDTO request) throws CustomException; //创建文件或镜像
        ["deprecate"] NodeFileDTO createNodeFileWithRequestOnly(UpdateNodeFileDTO request) throws CustomException; //创建文件或镜像
        ["deprecate"] NodeFileDTO updateNodeFile(NodeFileDTO src,UpdateNodeFileDTO request) throws CustomException; //更新文件或镜像
        ["deprecate"] NodeFileList listNodeFile(QueryNodeFileDTO query) throws CustomException; //查询文件或镜像

        SummaryFileDTO summaryFile(QuerySummaryDTO query) throws CustomException; //查询总使用空间

        void deleteNodeById(string id,DeleteAskDTO request) throws CustomException; //删除树节点
        void deleteNodeByIdList(StringList idList,DeleteAskDTO request) throws CustomException; //删除树节点
        void deleteNode(SimpleNodeDTO node,DeleteAskDTO request) throws CustomException; //删除树节点
        void deleteNodeList(SimpleNodeList node,DeleteAskDTO request) throws CustomException; //删除树节点

        SimpleNodeDTO createNode(SimpleNodeDTO parent,UpdateNodeDTO request) throws CustomException; //创建节点
        SimpleNodeDTO createNodeWithRequestOnly(UpdateNodeDTO request) throws CustomException; //创建节点
        SimpleNodeDTO updateNodeSimple(SimpleNodeDTO src,UpdateNodeDTO request) throws CustomException; //更改节点属性

        SimpleNodeList listNode(QueryNodeDTO query) throws CustomException; //查询节点列表
        SimpleNodeList listChild(SimpleNodeDTO parent) throws CustomException; //查询直接子节点
        SimpleNodeList listChildren(SimpleNodeDTO parent) throws CustomException; //查询所有子节点
        SimpleNodeList listRoot(string accountId) throws CustomException; //查询根节点
        SimpleNodeDTO getNodeById(string id) throws CustomException; //获取单个节点
        SimpleNodeDTO getNodeByPath(string path) throws CustomException; //获取单个节点
        SimpleNodeDTO getNodeByFuzzyPath(string fuzzyPath) throws CustomException; //获取单个节点
        SimpleNodeList listOldNode(QueryNodeDTO query) throws CustomException; //查询sky_driver中的节点信息

        FullNodeDTO getNodeInfo(SimpleNodeDTO node,QueryNodeInfoDTO request) throws CustomException; //查询节点详细信息

        SimpleNodeDTO updateNode(SimpleNodeDTO src,SimpleNodeDTO parent,UpdateNodeDTO request) throws CustomException; //更改节点属性
    };
};