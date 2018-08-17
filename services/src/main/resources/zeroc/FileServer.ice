#pragma once
#include <data/FileServerData.ice>
#include <Notice.ice>
#include <Storage.ice>

[["java:package:com.maoding.fileServer"]]
module zeroc {
    interface FileService {
        long getFileLength(NodeFileDTO file,bool readOnly) throws CustomException; //查询文件的实际长度

        void setStorageService(StorageService* storageService) throws CustomException; //设置文件服务器使用的节点服务器

        //为保持兼容性，提供与commonService相同的接口
        VersionDTO getNewestClient() throws CustomException; //获取与此文件服务器匹配的最新客户端版本
        long getVersionLength(VersionDTO version) throws CustomException; //获取版本文件的长度
        FileDataDTO readVersion(VersionDTO version,long pos,int size) throws CustomException; //读取指定版本的升级文件

        void flushBuffer() throws CustomException; //更新写缓存
        SummaryFileDTO summaryFile(QuerySummarySimpleDTO query) throws CustomException; //查询文件所占用空间
        void clearAll(AccountDTO account) throws CustomException; //整理文件服务器上的文件，并清理不再用到的文件
        void clearKey(AccountDTO account,string key) throws CustomException; //整理文件服务器上的文件，并清理不再用到的文件
        CANodeList listDesignNode(AccountDTO account) throws CustomException; //查询可提交的设计文档
        CANodeList listCANode(AccountDTO account) throws CustomException; //查询可校审的文档

        CommitListResultDTO checkNodeListRequest(CANodeList srcList) throws CustomException; //提交文件
        CommitListResultDTO checkNodeListRequestForAccount(AccountDTO account,CANodeList srcList) throws CustomException; //提交文件
        SimpleNodeDTO checkNodeRequest(CANodeDTO src) throws CustomException; //提交文件
        SimpleNodeDTO checkNodeRequestForAccount(AccountDTO account,CANodeDTO src) throws CustomException; //提交文件

        CommitListResultDTO auditNodeListRequest(CANodeList srcList) throws CustomException; //提交文件
        CommitListResultDTO auditNodeListRequestForAccount(AccountDTO account,CANodeList srcList) throws CustomException; //提交文件
        SimpleNodeDTO auditNodeRequest(CANodeDTO src) throws CustomException; //提交文件
        SimpleNodeDTO auditNodeRequestForAccount(AccountDTO account,CANodeDTO src) throws CustomException; //提交文件

        CommitListResultDTO askCANodeListRequest(CANodeList srcList) throws CustomException; //提交文件
        CommitListResultDTO askCANodeListRequestForAccount(AccountDTO account,CANodeList srcList) throws CustomException; //提交文件
        SimpleNodeDTO askCANodeRequest(CANodeDTO src) throws CustomException; //提交文件
        SimpleNodeDTO askCANodeRequestForAccount(AccountDTO account,CANodeDTO src) throws CustomException; //提交文件

        CommitListResultDTO requestCommitListForAccount(AccountDTO account,CANodeList srcList) throws CustomException; //提交文件
        SimpleNodeDTO requestCommitForAccount(AccountDTO account,CANodeDTO src) throws CustomException; //提交文件

        CommitListResultDTO requestIssueListForAccount(AccountDTO account,CANodeList srcList, SimpleNodeDTO parent) throws CustomException; //提交文件
        SimpleNodeDTO requestIssueForAccount(AccountDTO account,CANodeDTO src, SimpleNodeDTO parent) throws CustomException; //提交文件

        long getTime() throws CustomException; //获取服务器时间

        void restartWebRole(WebRoleDTO webRole) throws CustomException; //设置角色工作状态为未完成
        void finishWebRole(WebRoleDTO webRole) throws CustomException; //设置角色工作状态为完成
        void setWebRoleStatus(WebRoleDTO webRole, string statusId) throws CustomException; //设置角色工作状态
        WebRoleDTO getWebRole(AccountDTO account,SimpleNodeDTO node) throws CustomException; //根据node和账号获取角色
        WebRoleList listWebRoleTask(AccountDTO account) throws CustomException; //列出用户在生产任务中担任的角色
        WebRoleList listAllWebRole(AccountDTO account) throws CustomException; //列出用户在生产任务中担任的角色

        AnnotateDTO createAnnotateCheck(AccountDTO account,SimpleNodeDTO node,AnnotateRequestDTO request) throws CustomException; //创建校验意见
        AnnotateDTO createAnnotateAudit(AccountDTO account,SimpleNodeDTO node,AnnotateRequestDTO request) throws CustomException; //创建审核意见
        AnnotateDTO createAnnotate(AccountDTO account,SimpleNodeDTO node,AnnotateRequestDTO request) throws CustomException; //创建校审意见
        AnnotateDTO updateAnnotate(AccountDTO account,AnnotateDTO annotate,AnnotateRequestDTO query) throws CustomException; //更新校审意见
        AnnotateList listAnnotate(AccountDTO account,QueryAnnotateDTO query) throws CustomException; //查询校审意见
        NodeFileDTO addAccessory(AccountDTO account,AnnotateDTO annotate,AccessoryRequestDTO request) throws CustomException; //添加附件申请
        void deleteAccessory(AccountDTO account,AnnotateDTO annotate,NodeFileDTO accessory) throws CustomException; //删除附件申请

        string getNodePath(SimpleNodeDTO node) throws CustomException; //查询节点路径
        string getNodePathForAccount(AccountDTO account,SimpleNodeDTO node) throws CustomException; //查询节点路径
        HistoryList listHistory(SimpleNodeDTO node,long startTime,long endTime) throws CustomException; //查询节点历史信息
        HistoryList listHistoryForAccount(AccountDTO account,SimpleNodeDTO node,long startTime,long endTime) throws CustomException; //查询节点历史信息
        NodeFileDTO getFileInfo(SimpleNodeDTO node) throws CustomException; //查询节点文件信息
        NodeFileDTO getFileInfoForAccount(AccountDTO account,SimpleNodeDTO node) throws CustomException; //查询节点文件信息
        NodeTextDTO getTextInfo(SimpleNodeDTO node) throws CustomException; //查询节点文字信息
        NodeTextDTO getTextInfoForAccount(AccountDTO account,SimpleNodeDTO node) throws CustomException; //查询节点文字信息
        FullNodeDTO getFullNodeWithHis(SimpleNodeDTO node) throws CustomException; //查询带历史节点详细信息
        FullNodeDTO getFullNodeWithHisForAccount(AccountDTO account,SimpleNodeDTO node) throws CustomException; //查询带历史节点详细信息
        FullNodeDTO getFullNode(SimpleNodeDTO node) throws CustomException; //通过节点查询文件完整信息
        FullNodeDTO getFullNodeForAccount(AccountDTO account,SimpleNodeDTO node) throws CustomException; //通过节点查询文件完整信息
        FullNodeDTO getNodeInfo(SimpleNodeDTO node,QueryNodeInfoDTO request) throws CustomException; //查询节点详细信息
        FullNodeDTO getNodeInfoForAccount(AccountDTO account,SimpleNodeDTO node,QueryNodeInfoDTO request) throws CustomException; //查询节点详细信息

        NodeFileList listFile(AccountDTO account,AskFileDTO query) throws CustomException; //查询文件
        NodeFileList listValidFile(AccountDTO account,AskValidFileDTO query) throws CustomException; //查询文件

        SimpleNodeList listChildNode(SimpleNodeDTO parent) throws CustomException; //查询子节点
        SimpleNodeList listChildNodeForAccount(AccountDTO account,SimpleNodeDTO parent) throws CustomException; //查询子节点
        SimpleNodeList listChildrenNode(SimpleNodeDTO parent) throws CustomException; //查询所有子节点
        SimpleNodeList listChildrenNodeForAccount(AccountDTO account,SimpleNodeDTO parent) throws CustomException; //查询所有子节点

        SimpleNodeList listWebArchiveDir(string projectId) throws CustomException; // 获取网站空间的归档目录树
        SimpleNodeList listWebArchiveDirForAccount(AccountDTO account,string projectId) throws CustomException; // 获取网站空间的归档目录树

        SimpleNodeDTO changeNodeOwner(SimpleNodeDTO src,UserDTO dstOwner) throws CustomException; //更改文件所有者
        SimpleNodeDTO changeNodeOwnerForAccount(AccountDTO account,SimpleNodeDTO src,UserDTO dstOwner) throws CustomException; //更改文件所有者

        bool login(LoginDTO loginInfo) throws CustomException; //登录
        StringList setNoticeClient(string userId,NoticeClient* client) throws CustomException; //登录

        IdNameList listMajor() throws CustomException; //列出可用专业
        IdNameList listMajorForAccount(AccountDTO account) throws CustomException; //列出可用专业
        IdNameList listAction() throws CustomException; //列出可用操作
        IdNameList listActionForAccount(AccountDTO account) throws CustomException; //列出可用操作
        ProjectRoleList listProjectRoleByProjectId(string projectId) throws CustomException; //获取项目的参与角色列表
        ProjectRoleList listProjectRoleByProjectIdForAccount(AccountDTO account,string projectId) throws CustomException; //获取项目的参与角色列表

        CommitListResultDTO issueNodeList(SimpleNodeList srcList,CommitRequestDTO request) throws CustomException; //提交文件到web
        CommitListResultDTO issueNodeListForAccount(AccountDTO account,SimpleNodeList srcList,CommitRequestDTO request) throws CustomException; //提交文件到web
        SimpleNodeDTO issueNode(SimpleNodeDTO src,CommitRequestDTO request) throws CustomException; //提交文件到web
        SimpleNodeDTO issueNodeForAccount(AccountDTO account,SimpleNodeDTO src,CommitRequestDTO request) throws CustomException; //提交文件到web

        CommitListResultDTO commitNodeList(SimpleNodeList srcList,CommitRequestDTO request) throws CustomException; //提交文件
        CommitListResultDTO commitNodeListForAccount(AccountDTO account,SimpleNodeList srcList,CommitRequestDTO request) throws CustomException; //提交文件
        SimpleNodeDTO commitNode(SimpleNodeDTO src,CommitRequestDTO request) throws CustomException; //提交文件
        SimpleNodeDTO commitNodeForAccount(AccountDTO account,SimpleNodeDTO src,CommitRequestDTO request) throws CustomException; //提交文件

        SimpleNodeDTO createVersion(AccountDTO account, SimpleNodeDTO src, string path, CommitRequestDTO request) throws CustomException; //创建版本
        SimpleNodeDTO updateVersion(AccountDTO account, SimpleNodeDTO src, SimpleNodeDTO dst, CommitRequestDTO request) throws CustomException; //创建版本
        CommitListResultDTO updateNodeList(AccountDTO account,SimpleNodeList srcList,CommitRequestDTO request) throws CustomException; //更改节点属性

        bool deleteNode(SimpleNodeDTO src) throws CustomException; //删除节点
        bool deleteNodeForAccount(AccountDTO account,SimpleNodeDTO src) throws CustomException; //删除节点

        void setFileLength(AccountDTO account,NodeFileDTO file,long fileLength) throws CustomException; //设置文件长度
        bool setNodeLength(SimpleNodeDTO src,long fileLength) throws CustomException; //设置文件长度
        bool setNodeLengthForAccount(AccountDTO account,SimpleNodeDTO src,long fileLength) throws CustomException; //设置文件长度
        bool setFullNodeLength(FullNodeDTO src,long fileLength) throws CustomException; //设置文件长度
        bool setFullNodeLengthForAccount(AccountDTO account,FullNodeDTO src,long fileLength) throws CustomException; //设置文件长度

        void releaseFile(AccountDTO account,NodeFileDTO file,string path) throws CustomException; //发布文件
        bool releaseNode(SimpleNodeDTO src,long fileLength) throws CustomException; //用可写版本覆盖只读版本
        bool releaseNodeForAccount(AccountDTO account,SimpleNodeDTO  src,long fileLength) throws CustomException; //用可写版本覆盖只读版本

        void reloadFile(AccountDTO account,NodeFileDTO file,string path) throws CustomException; //还原文件
        bool reloadNode(SimpleNodeDTO src) throws CustomException; //用只读版本覆盖可写版本
        bool reloadNodeForAccount(AccountDTO account,SimpleNodeDTO  src) throws CustomException; //用只读版本覆盖可写版本

        int writeFile(AccountDTO account,NodeFileDTO file,FileDataDTO data,string path) throws CustomException; //写入文件
        int writeFileAndRelease(AccountDTO account,NodeFileDTO file,FileDataDTO data,string path,long fileLength) throws CustomException; //写入文件并发布文件
        int writeAccessory(AccountDTO account,NodeFileDTO file,FileDataDTO data) throws CustomException; //写入文件
        int writeNode(SimpleNodeDTO src,FileDataDTO data) throws CustomException; //写入文件节点
        int writeNodeForAccount(AccountDTO account,SimpleNodeDTO src,FileDataDTO data) throws CustomException; //写入文件节点

        FileDataDTO readFile(AccountDTO account,NodeFileDTO file,long pos,int size) throws CustomException; //读取文件
        FileDataDTO readNode(SimpleNodeDTO src,long pos,int size) throws CustomException; //读出文件节点
        FileDataDTO readNodeForAccount(AccountDTO account,SimpleNodeDTO src,long pos,int size) throws CustomException; //读出文件节点

        bool isEmpty(SimpleNodeDTO node) throws CustomException; //判断节点是否存在子节点
        bool isExist(string path) throws CustomException; //判断节点是否存在
        bool isExistForAccount(AccountDTO account,string path) throws CustomException; //判断节点是否存在

        SimpleNodeDTO moveNode(SimpleNodeDTO src,SimpleNodeDTO dstParent,MoveNodeRequestDTO request) throws CustomException; //移动节点
        SimpleNodeDTO moveNodeForAccount(AccountDTO account,SimpleNodeDTO src,SimpleNodeDTO dstParent,MoveNodeRequestDTO request) throws CustomException; //移动节点

        SimpleNodeDTO createDirectory(SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建目录,返回节点信息
        SimpleNodeDTO createDirectoryForAccount(AccountDTO account,SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建目录,返回节点信息
        SimpleNodeDTO createFile(SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建目录,返回节点信息
        SimpleNodeDTO createFileForAccount(AccountDTO account,SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建目录,返回节点信息
        SimpleNodeDTO createNode(SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建树节点,返回节点信息
        SimpleNodeDTO createNodeForAccount(AccountDTO account,SimpleNodeDTO parent,CreateNodeRequestDTO request) throws CustomException; //创建树节点,返回节点信息

        SimpleNodeDTO getNodeById(string id) throws CustomException; //查询节点信息
        SimpleNodeDTO getNodeByIdForAccount(AccountDTO account,string id) throws CustomException; //查询节点信息
        SimpleNodeDTO getNodeByPath(string path) throws CustomException; //查询节点信息
        SimpleNodeDTO getNodeByPathForAccount(AccountDTO account,string path) throws CustomException; //查询节点信息
        SimpleNodeDTO getNodeByFuzzyPath(string fuzzyPath) throws CustomException; //查询节点信息
        SimpleNodeDTO getNodeByFuzzyPathForAccount(AccountDTO account,string fuzzyPath) throws CustomException; //查询节点信息

        SimpleNodeList listRootNode() throws CustomException; //查询根节点
        SimpleNodeList listRootNodeForAccount(AccountDTO account) throws CustomException; //查询根节点
        SimpleNodeList listAllNode() throws CustomException; //查询所有节点
        SimpleNodeList listAllNodeForAccount(AccountDTO account) throws CustomException; //查询所有节点

        SimpleNodeList listNode(QueryNodeDTO query) throws CustomException; //查询指定节点
        SimpleNodeList listNodeForAccount(AccountDTO account,QueryNodeDTO query) throws CustomException; //查询指定节点
    };
};