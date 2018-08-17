package com.maoding.storage;

import com.maoding.common.CheckService;
import com.maoding.common.LocalConstService;
import com.maoding.common.zeroc.CustomException;
import com.maoding.common.zeroc.DeleteAskDTO;
import com.maoding.common.zeroc.ErrorCode;
import com.maoding.common.zeroc.QueryAskDTO;
import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreUtils.*;
import com.maoding.storage.dao.*;
import com.maoding.storage.dto.StorageEntityUnionDTO;
import com.maoding.storage.entity.*;
import com.maoding.storage.zeroc.*;
import com.maoding.user.zeroc.AccountDTO;
import com.zeroc.Ice.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/2 14:21
 * 描    述 :
 */
@Service("storageService")
@Transactional(rollbackFor = Exception.class)
public class StorageServiceImpl extends CoreLocalService implements StorageService{

    @Autowired
    private StorageDao storageDao;

    @Autowired
    private StorageTreeDao storageTreeDao;

    @Autowired
    private StorageFileDao storageFileDao;

    @Autowired
    private StorageFileHisDao storageFileHisDao;

    @Autowired
    private ElementListDao elementListDao;

    @Autowired
    private AnnotateTreeDao annotateTreeDao;

    @Autowired
    private AttachmentListDao attachmentListDao;

    @Autowired
    private AnnotateDao annotateDao;

    @Autowired
    private ElementDao elementDao;

    private Map<String,List<SimpleNodeDTO>> simpleNodeMap = new HashMap<>();
    private Map<String,Integer> queryTimesMap = new HashMap<>();
    private String lastKey = null;

    @Override
    public List<NodeFileDTO> listFile(QueryNodeDTO query, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        List<NodeFileDTO> fileList = storageDao.listFile(BeanUtils.cleanProperties(query));

        log.info("\t----> listFile:" + (System.currentTimeMillis()-t) + "ms");
        return fileList;
    }

    @Override
    public List<HistoryDTO> listHistory(QueryHistoryDTO query, Current current) throws CustomException {
        return null;
    }

    @Override
    @Deprecated
    public List<CANodeDTO> listCANode(@NotNull QueryCANodeDTO query, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        List<CANodeDTO> fileList = storageDao.listCANode(BeanUtils.cleanProperties(query));

        log.info("\t----> listCANode:" + (System.currentTimeMillis()-t) + "ms");
        return fileList;
    }

    @Override
    public List<FullNodeDTO> listFullNode(QueryNodeDTO query, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        List<FullNodeDTO> fullNodeList = storageDao.listFullNode(BeanUtils.cleanProperties(query));

        log.info("\t----> listFullNode:" + (System.currentTimeMillis()-t) + "ms");
        return fullNodeList;
    }

    @Override
    public List<EmbedElementDTO> listEmbedElement(@NotNull QueryAskDTO query, Current current) throws CustomException {
        return elementDao.listElement(query);
    }

    @Override
    public List<NodeFileDTO> listNodeFile(QueryNodeFileDTO query, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        List<NodeFileDTO> fileList = storageDao.listNodeFile(BeanUtils.cleanProperties(query));

        log.info("\t----> listNodeFile花费时间:" + (System.currentTimeMillis()-t) + "ms");
        return fileList;
    }

    @Override
    public EmbedElementDTO createEmbedElement(UpdateElementDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        ElementEntity entity = BeanUtils.createCleanFrom(request,ElementEntity.class);
        String id = entity.getId();
        elementListDao.insert(entity);
        EmbedElementDTO result = BeanUtils.createCleanFrom(entity,EmbedElementDTO.class);

        log.info("\t----> createEmbedElement花费时间:" + (System.currentTimeMillis()-t) + "ms");
        return result;
    }

    @Override
    @Deprecated
    public NodeFileDTO updateNodeFile(@NotNull NodeFileDTO file, @NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
//        final String QUERY_FIELD_ID = "id";
//        final String QUERY_MAIN_FIELD_ID = "mainFileId";
//        final String QUERY_MIRROR_SERVER_TYPE_ID = "mirrorServerTypeId";
//        final String QUERY_MIRROR_SERVER_ADDRESS = "mirrorServerAddress";
//        final String QUERY_MIRROR_BASE_DIR = "mirrorBaseDir";
//
//        CheckService.check(StringUtils.isNotEmpty(file.getId()),ErrorCode.DataIsInvalid);
//
//        long t = System.currentTimeMillis();
//
//        request = BeanUtils.cleanProperties(request);
//
//        CheckService.check(StringUtils.isNotEmpty(file.getId()), ErrorCode.InvalidParameter,"updateNodeFile");
//        //更新节点信息
//        Map<String,Object> queryFile = new HashMap<>();
//        queryFile.put(QUERY_FIELD_ID,file.getId());
//        StorageFileEntity fileEntity = storageDao.selectFileEntity(queryFile);
//        if (fileEntity == null) {
//            clearBufferAndWarning();
//        }
//        BeanUtils.copyCleanProperties(request, fileEntity);
//        fileEntity.update();
//        storageFileDao.update(fileEntity);
//
//        //更新镜像信息
//        if (isMirrorInfoValid(request)){
//            Map<String,Object> queryMirror = new HashMap<>();
//            if (StringUtils.isNotEmpty(file.getId())) {
//                queryMirror.put(QUERY_MAIN_FIELD_ID,file.getId());
//            }
//            if (StringUtils.isNotEmpty(request.getMirrorTypeId())) {
//                queryMirror.put(QUERY_MIRROR_SERVER_TYPE_ID, request.getMirrorTypeId());
//            }
//            if (StringUtils.isNotEmpty(request.getMirrorAddress())) {
//                queryMirror.put(QUERY_MIRROR_SERVER_ADDRESS, request.getMirrorAddress());
//            }
//            if (StringUtils.isNotEmpty(request.getMirrorBaseDir())) {
//                queryMirror.put(QUERY_MIRROR_BASE_DIR, request.getMirrorBaseDir());
//            }
//            StorageFileEntity mirrorEntity = storageDao.selectFileEntity(queryMirror);
//            if (mirrorEntity == null){
//                mirrorEntity = new StorageFileEntity();
//                updateMirrorEntity(request,file.getId(),mirrorEntity);
//                storageFileDao.insert(mirrorEntity);
//            } else {
//                updateMirrorEntity(request,file.getId(),mirrorEntity);
//                storageFileDao.update(mirrorEntity);
//            }
//        }
//        BeanUtils.copyCleanProperties(request,file);
//
//        //更新节点长度和md5
//        if ((StringUtils.isNotEmpty(request.getFileLength()) || (StringUtils.isNotEmpty(request.getFileMd5())))) {
//            StorageTreeEntity nodeEntity = storageTreeDao.selectById(StringUtils.left(file.getId(), StringUtils.DEFAULT_ID_LENGTH));
//            if (nodeEntity == null) {
//                clearBufferAndWarning();
//            }
//            nodeEntity.setFileLength(fileEntity.getFileLength());
//            nodeEntity.setFileMd5(fileEntity.getFileMd5());
//            nodeEntity.update();
//            storageTreeDao.update(nodeEntity);
//        }
//
//        log.info("\t----> updateNodeFile花费时间:" + (System.currentTimeMillis()-t) + "ms");
        return file;
    }



    private void clearBufferAndWarning() throws CustomException{
        simpleNodeMap.clear();
        queryTimesMap.clear();
        CheckService.check(false,ErrorCode.DataNotFound);
    }

    private void clearBuffer(String srcPid, String dstPid){
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String, List<SimpleNodeDTO>> entry : simpleNodeMap.entrySet()){
            List<SimpleNodeDTO> list = entry.getValue();
            for (SimpleNodeDTO n : list){
                if (StringUtils.isSame(n.getPid(),srcPid) || (StringUtils.isSame(n.getPid(),dstPid))){
                    keyList.add(entry.getKey());
                    break;
                }
            }
        }
        for (String key : keyList) {
            simpleNodeMap.remove(key);
            queryTimesMap.remove(key);
        }
    }

    @Override
    public NodeFileDTO createFileWithId(UpdateNodeFileDTO request, String id, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        StorageFileEntity fileEntity = EntityUtils.replace(storageFileDao,StorageFileEntity.class,formatRequest(request),id);
        if (request.getHistoryUpdateRequest() != null){
            String mainId = StringUtils.getString(fileEntity.getMainFileId(),fileEntity.getId());
            createHistory(request.getHistoryUpdateRequest(),mainId);
        }
        NodeFileDTO file = BeanUtils.createCleanFrom(fileEntity,NodeFileDTO.class);

        log.info("\t----> createFileWithId:" + (System.currentTimeMillis()-t) + "ms");
        return file;
    }

    @Override
    public NodeFileDTO createFile(@NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
        return createFileWithId(request,null,current);
    }

    @Override
    public NodeFileDTO createMirror(@NotNull NodeFileDTO src, @NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
        UpdateNodeFileDTO mirrorRequest = BeanUtils.createCleanFrom(src,UpdateNodeFileDTO.class);
        BeanUtils.copyCleanProperties(request,mirrorRequest);
        return createFile(mirrorRequest,current);
    }

    @Override
    public NodeFileDTO updateFile(@NotNull NodeFileDTO src, @NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        CheckService.check(isValid(src),ErrorCode.DataIsInvalid);
        StorageFileEntity fileEntity = EntityUtils.replace(storageFileDao,StorageFileEntity.class,formatRequest(request),src.getId());
        if (request.getHistoryUpdateRequest() != null){
            createHistory(request.getHistoryUpdateRequest(),StringUtils.getString(fileEntity.getMainFileId(),fileEntity.getId()));
        }
        NodeFileDTO file = BeanUtils.createCleanFrom(fileEntity,NodeFileDTO.class);

        log.info("\t----> updateFile:" + (System.currentTimeMillis()-t) + "ms");
        return file;
    }

    @Override
    public NodeFileDTO createNodeFileWithRequestOnly(@NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
        return createNodeFile(null,request,current);
    }

    @Override
    public SummaryFileDTO summaryFile(@NotNull QuerySummaryDTO query, Current current) throws CustomException {
        List<SummaryFileCompanyDTO> list = BeanUtils.cleanProperties(storageDao.summaryFile(query));
        SummaryFileDTO result = new SummaryFileDTO();
        long totalAllocSize = 0;
        long totalUsageSize = 0;
        if (ObjectUtils.isNotEmpty(list)){
            for (SummaryFileCompanyDTO summary : list){
                //汇总各公司的总分配空间，计算用户所在的所有公司的总空间
                long usageSize = 0;
                totalAllocSize += summary.getAllocSize();
                List<SummaryFileServerDTO> serverList = summary.getServerList();
                if (ObjectUtils.isNotEmpty(serverList)){
                    //计算公司下文件服务器的使用空间，汇总为公司的已使用空间
                    for (SummaryFileServerDTO server : serverList){
                        usageSize += server.getUsageSize();
                    }
                }

                //如果参数内排除了部分文件服务器，默认为这部分服务器是sky_driver，需要把sky_driver内的已用空间添加上
                if (StringUtils.isNotEmpty(query.getNotServerTypeId())){
                    usageSize += summary.getSkyUsageSize();
                }
                summary.setUsageSize(usageSize);

                //添加此公司已使用空间，汇总用户所在公司的所有空间
                totalUsageSize += usageSize;
            }
        }
        result.setTotalAllocSize(totalAllocSize);
        result.setTotalUsageSize(totalUsageSize);
        result.setCompanyList(list);
        return result;
    }

    @Override
    public EmbedElementDTO updateEmbedElement(@NotNull EmbedElementDTO src, @NotNull UpdateElementDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        ElementEntity entity = elementListDao.selectById(src.getId());
        CheckService.check(entity != null,ErrorCode.DataNotFound);
        BeanUtils.copyCleanProperties(request,entity);
        entity.update();
        int n = elementListDao.update(entity);
        CheckService.check (n == 1,ErrorCode.DataIsInvalid,"updateEmbedElement");
        BeanUtils.copyCleanProperties(entity,src);

        log.info("\t----> updateEmbedElement:" + (System.currentTimeMillis()-t) + "ms");
        return src;
    }

    private void addAttachmentList(@NotNull AnnotateEntity annotateEntity, List<String> elementIdList, List<String> fileIdList){
        List<AttachmentEntity> attachmentList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(elementIdList)) {
            for (String id : elementIdList) {
                AttachmentEntity attachmentEntity = BeanUtils.createCleanFrom(annotateEntity, AttachmentEntity.class);
                attachmentEntity.resetId();
                attachmentEntity.setAnnotateId(annotateEntity.getId());
                attachmentEntity.setAttachmentElementId(id);
                attachmentList.add(attachmentEntity);
            }
        }
        if (ObjectUtils.isNotEmpty(fileIdList)) {
            for (String id : fileIdList) {
                AttachmentEntity attachmentEntity = BeanUtils.createCleanFrom(annotateEntity, AttachmentEntity.class);
                attachmentEntity.resetId();
                attachmentEntity.setAnnotateId(annotateEntity.getId());
                attachmentEntity.setAttachmentFileId(id);
                attachmentList.add(attachmentEntity);
            }
        }
        if (ObjectUtils.isNotEmpty(attachmentList)){
            int n = attachmentListDao.insertList(attachmentList);
            assert (n == attachmentList.size());
        }

    }

    @Override
    public AnnotateDTO createAnnotate(NodeFileDTO file, @NotNull UpdateAnnotateDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        //建立文件注解记录
        AnnotateEntity annotateEntity = BeanUtils.createCleanFrom(request,AnnotateEntity.class);
        if (file != null) {
            annotateEntity.setFileId(file.getId());
            if (StringUtils.isNotEmpty(file.getMainFileId())) {
                annotateEntity.setMainFileId(file.getMainFileId());
            } else {
                annotateEntity.setMainFileId(file.getId());
            }
        }

        //添加文件注解附件
        addAttachmentList(annotateEntity,request.getAddElementIdList(),request.getAddFileIdList());

        int n = annotateTreeDao.insert(annotateEntity);
        assert (n == 1);
        AnnotateDTO annotate = BeanUtils.createCleanFrom(annotateEntity,AnnotateDTO.class);

        log.info("\t----> createAnnotate:" + (System.currentTimeMillis()-t) + "ms");
        return annotate;
    }

    @Override
    public List<AnnotateDTO> listAnnotate(@NotNull QueryAnnotateDTO query, Current current) throws CustomException {
        return annotateDao.listAnnotate(BeanUtils.cleanProperties(query));
    }

    @Override
    public AnnotateDTO updateAnnotate(@NotNull AnnotateDTO src, @NotNull UpdateAnnotateDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        //更新文件注解记录
        AnnotateEntity annotateEntity = annotateTreeDao.selectById(StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH));
        BeanUtils.copyCleanProperties(request,annotateEntity);
        annotateEntity.update();

        //添加附件
        addAttachmentList(annotateEntity,request.getAddElementIdList(),request.getAddFileIdList());

        //删除附件
        if (ObjectUtils.isNotEmpty(request.getDelAttachmentIdList())) {
            List<String> delElementIdList = new ArrayList<>();
            delElementIdList.addAll(request.getDelAttachmentIdList());
            attachmentListDao.deleteAttachment(annotateEntity.getId(),delElementIdList,request.getLastModifyUserId());
        }

        int n = annotateTreeDao.update(annotateEntity);
        assert (n == 1);
        BeanUtils.copyCleanProperties(annotateEntity,src);

        log.info("\t----> updateAnnotate:" + (System.currentTimeMillis()-t) + "ms");
        return src;
    }

    @Override
    public NodeFileDTO createNodeFile(NodeFileDTO src, @NotNull UpdateNodeFileDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        log.info("\t----> createNodeFile:" + (System.currentTimeMillis()-t) + "ms");
        return src;
    }

    @Override
    public FullNodeDTO getNodeInfo(@NotNull SimpleNodeDTO node, @NotNull QueryNodeInfoDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        CheckService.check(isValid(node),ErrorCode.DataIsInvalid);
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(node.getId());
        if (request.getTextQuery() != null){
            query.setNeedPath(LocalConstService.MODE_TRUE);
            query.setNeedProjectName(LocalConstService.MODE_TRUE);
            query.setNeedTaskName(LocalConstService.MODE_TRUE);
            query.setNeedOwnerName(LocalConstService.MODE_TRUE);
        }
        if (request.getFileQuery() != null) {
            query.setNeedFileInfo(LocalConstService.MODE_TRUE);
        }
        if (request.getHistoryQuery() != null) {
            query.setNeedFileHistoryInfo(LocalConstService.MODE_TRUE);
        }
        List<FullNodeDTO> fullNodeList = storageDao.listFullNode(BeanUtils.cleanProperties(query));
        FullNodeDTO fullNode = (ObjectUtils.isNotEmpty(fullNodeList)) ? fullNodeList.get(0) : null;

        log.info("\t----> getNodeInfo花费时间:" + (System.currentTimeMillis()-t) + "ms");
        return fullNode;
    }

    private QueryNodeInfoDTO cleanQueryNodeInfo(@NotNull QueryNodeInfoDTO request){
        request.setFileQuery(BeanUtils.cleanProperties(request.getFileQuery()));
        return request;
    }

    @Override
    public List<SimpleNodeDTO> listOldNode(@NotNull QueryNodeDTO query, Current current) {
        return storageDao.listWebArchiveDir(BeanUtils.cleanProperties(query));
    }

    @Deprecated
    private StorageFileEntity updateMirrorEntity(@NotNull UpdateNodeFileDTO request, @NotNull String fileId, @NotNull StorageFileEntity mirrorEntity){
//        request = BeanUtils.cleanProperties(request);
//        assert (isMirrorInfoValid(request));
//        if (StringUtils.isEmpty(request.getFileTypeId())){
//            mirrorEntity.setFileTypeId(ConstService.STORAGE_FILE_TYPE_MIRROR.toString());
//        } else {
//            mirrorEntity.setFileTypeId(request.getFileTypeId());
//        }
//        mirrorEntity.setServerTypeId(request.getMirrorTypeId());
//        mirrorEntity.setServerAddress(request.getMirrorAddress());
//        mirrorEntity.setBaseDir(request.getMirrorBaseDir());
//        mirrorEntity.setReadOnlyKey(request.getReadOnlyMirrorKey());
//        mirrorEntity.setWritableKey(request.getWritableMirrorKey());
//        assert (StringUtils.isNotEmpty(fileId));
//        mirrorEntity.setMainFileId(fileId);
//        if (StringUtils.isNotEmpty(request.getFileLength())) {
//            mirrorEntity.setFileLength(DigitUtils.parseLong(request.getFileLength()));
//        }
//        if (StringUtils.isNotEmpty(request.getFileMd5())) {
//            mirrorEntity.setFileMd5(request.getFileMd5());
//        }
//        if (StringUtils.isNotEmpty(request.getLastModifyUserId())){
//            mirrorEntity.setLastModifyUserId(request.getLastModifyUserId());
//        }
//        if (StringUtils.isNotEmpty(request.getLastModifyRoleId())){
//            mirrorEntity.setLastModifyRoleId(request.getLastModifyRoleId());
//        }
        return mirrorEntity;
    }

    private StorageFileEntity updateMirrorEntity(@NotNull UpdateNodeDTO request, @NotNull String fileId, @NotNull StorageFileEntity mirrorEntity){
        UpdateNodeFileDTO updateFileRequest = BeanUtils.createCleanFrom(request,UpdateNodeFileDTO.class);
        updateMirrorEntity(updateFileRequest,fileId,mirrorEntity);
        return mirrorEntity;
    }

    private StorageFileEntity selectMirrorFileEntity(@NotNull String fileId, String serverTypeId, String serverAddress, String baseDir){
        StorageFileEntity query = new StorageFileEntity();
        query.clear();
        query.setServerTypeId(serverTypeId);
        query.setServerAddress(serverAddress);
        query.setBaseDir(baseDir);
        query.setMainFileId(fileId);
        List<StorageFileEntity> list = storageFileDao.select(query);
        return (ObjectUtils.isNotEmpty(list)) ? list.get(0) : null;
    }

    private boolean isHisInfoValid(@NotNull UpdateNodeDTO request){
        return (LocalConstService.STORAGE_ACTION_TYPE_UNKNOWN != DigitUtils.parseShort(request.getActionTypeId()))
                || (ObjectUtils.isNotEmpty(request.getRemark()));

    }

    private boolean isFileInfoValid(@NotNull UpdateNodeDTO request){
        return (LocalConstService.FILE_SERVER_TYPE_UNKNOWN != DigitUtils.parseShort(request.getServerTypeId()))
                || (ObjectUtils.isNotEmpty(request.getServerAddress()))
                || (ObjectUtils.isNotEmpty(request.getReadOnlyKey()))
                || (ObjectUtils.isNotEmpty(request.getWritableKey()))
                || (DigitUtils.parseLong(request.getFileLength()) > 0)
                || (StringUtils.isNotEmpty(request.getFileMd5()))
                || (StringUtils.isNotEmpty(request.getIsPassDesign()))
                || (StringUtils.isNotEmpty(request.getIsPassCheck()))
                || (StringUtils.isNotEmpty(request.getIsPassAudit()))
                || isHisInfoValid(request)
                || isMirrorInfoValid(request);
    }

    private boolean isMirrorInfoValid(@NotNull UpdateNodeDTO request) {
        return (ObjectUtils.isNotEmpty(request.getReadOnlyMirrorKey()))
                || (ObjectUtils.isNotEmpty(request.getWritableMirrorKey()));
    }

    @Deprecated
    private boolean isMirrorInfoValid(@NotNull UpdateNodeFileDTO request) {
        return false;
//        return (ObjectUtils.isNotEmpty(request.getReadOnlyMirrorKey()))
//                || (ObjectUtils.isNotEmpty(request.getWritableMirrorKey()));
    }

    private boolean isValid(SimpleNodeDTO node) {
        return (node != null) && (StringUtils.isNotEmpty(node.getId()));
    }

    private boolean isValid(NodeFileDTO file) {
        return (file != null) && (StringUtils.isNotEmpty(file.getId()));
    }

    private StorageTreeEntity insertPathNodeList(SimpleNodeDTO parent, String path, String accountId, String accountRoleId) {
        //获取父节点
        while (StringUtils.isStartWith(path,StringUtils.SPLIT_PATH)) {
            path = StringUtils.substring(path,StringUtils.SPLIT_PATH.length());
        }
        StorageTreeEntity lastEntity = null;
        if (isValid(parent)) {
            lastEntity = storageTreeDao.selectById(StringUtils.left(parent.getId(),StringUtils.DEFAULT_ID_LENGTH));

        }
//        if (lastEntity != null) {
//            path = StringUtils.formatPath(lastEntity.getPath() + StringUtils.SPLIT_PATH + path);
//        }
        String taskId = (parent != null) ? parent.getTaskId() : null;
//        if (StringUtils.isNotEmpty(taskId) && StringUtils.isNotEmpty(path)) {
//            lastEntity = storageTreeDao.selectByTaskIdAndFuzzyPath(taskId, path);
//        }
//        if (lastEntity != null) {
//            path = StringUtils.substring(path, StringUtils.length(lastEntity.getPath()) + StringUtils.SPLIT_PATH.length());
//        }

        //如果目标路径为空直接返回，否则将其拆解为数组
        if (StringUtils.isEmpty(path)) return lastEntity;
        String[] nodeNameArray = path.split(StringUtils.SPLIT_PATH);

        //插入中间节点
        List<StorageTreeEntity> nodeList = new ArrayList<>();
        StringBuilder pathBuilder = new StringBuilder();
        if (lastEntity != null) {
            pathBuilder.append(lastEntity.getPath());
        }

        for (String nodeName : nodeNameArray) {
            if (StringUtils.isEmpty(nodeName)) continue;
            if (pathBuilder.length() > 0) pathBuilder.append(StringUtils.SPLIT_PATH);
            pathBuilder.append(nodeName);
            StorageTreeEntity entity = new StorageTreeEntity();
            entity.reset();
            if (lastEntity != null) {
                entity.setPid(lastEntity.getId());
                if (StringUtils.isNotEmpty(lastEntity.getTypeId())) {
                    entity.setTypeId(LocalConstService.getPathType(lastEntity.getTypeId()));
                } else {
                    entity.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_DIR_UNKNOWN));
                }
                entity.setTaskId(lastEntity.getTaskId());
                entity.setProjectId(lastEntity.getProjectId());
            } else if (isValid(parent)){
                if (StringUtils.isNotEmpty(parent.getTypeId())) {
                    entity.setTypeId(LocalConstService.getPathType(parent.getTypeId()));
                } else {
                    entity.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_DIR_UNKNOWN));
                }
                entity.setTaskId(parent.getTaskId());
                entity.setProjectId(parent.getProjectId());
            } else { //添加根节点
                lastEntity = new StorageTreeEntity();
                lastEntity.reset();
                lastEntity.setPath(StringUtils.SPLIT_PATH);
                lastEntity.setNodeName(StringUtils.SPLIT_PATH);
                lastEntity.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_DIR_UNKNOWN));
                lastEntity.setLastModifyUserId(accountId);
                lastEntity.setLastModifyRoleId(accountRoleId);
                nodeList.add(BeanUtils.cleanProperties(lastEntity));
                pathBuilder = new StringBuilder(StringUtils.SPLIT_PATH).append(nodeName);

                entity.setPid(lastEntity.getId());
                entity.setTypeId(LocalConstService.getPathType(lastEntity.getTypeId()));
            }

            entity.setNodeName(nodeName);
            entity.setPath(StringUtils.formatPath(pathBuilder.toString()));
            entity.setOwnerUserId(accountId);
            entity.setLastModifyUserId(accountId);
            entity.setLastModifyRoleId(accountRoleId);
            nodeList.add(BeanUtils.cleanProperties(entity));
            lastEntity = entity;
        }
        if (!nodeList.isEmpty()) {
            int n = storageTreeDao.insertList(nodeList);
//            assert (n > 0);
        }

        return lastEntity;
    }

    private StorageEntityUnionDTO updateStorage(@NotNull StorageEntityUnionDTO nodeUnion, StorageTreeEntity parentEntity, SimpleNodeDTO parent, @NotNull UpdateNodeDTO request) {
        if (parentEntity != null) {
            nodeUnion.setPid(parentEntity.getId());
            String parentPath = parentEntity.getPath();
            if (StringUtils.isNotEmpty(parentPath)) {
                parentPath += StringUtils.SPLIT_PATH;
            }
            nodeUnion.setPath(StringUtils.formatPath(parentPath + nodeUnion.getNodeName()));
            if (LocalConstService.isDirectoryType(nodeUnion.getTypeId())) {
                nodeUnion.setTypeId(LocalConstService.getPathType(parentEntity.getTypeId()));
            } else {
                nodeUnion.setTypeId(LocalConstService.getFileType(parentEntity.getTypeId()));
            }
            nodeUnion.setTaskId(parentEntity.getTaskId());
            nodeUnion.setProjectId(parentEntity.getProjectId());
        } else {
            nodeUnion.setPid("-");
            nodeUnion.setPath(nodeUnion.getNodeName());
            if (isValid(parent)) {
                if (LocalConstService.isDirectoryType(nodeUnion.getTypeId())) {
                    nodeUnion.setTypeId(LocalConstService.getPathType(parent.getTypeId()));
                } else {
                    nodeUnion.setTypeId(LocalConstService.getFileType(parent.getTypeId()));
                }
                if (StringUtils.isEmpty(nodeUnion.getTaskId())) {
                    nodeUnion.setTaskId(parent.getTaskId());
                }
                if (StringUtils.isEmpty(nodeUnion.getProjectId())) {
                    nodeUnion.setProjectId(parent.getProjectId());
                }
            }
        }
        return nodeUnion;
    }

    private UpdateNodeDTO formatRequest(@NotNull UpdateNodeDTO request){
        request = BeanUtils.cleanProperties(request);
        if (request == null){
            return null;
        }
        if (request.getUpdateFileRequest() != null){
            UpdateNodeFileDTO updateFileRequest = request.getUpdateFileRequest();
            BeanUtils.copyCleanProperties(request,updateFileRequest);
            updateFileRequest =  BeanUtils.cleanProperties(updateFileRequest);
            request.setUpdateFileRequest(updateFileRequest);
        }
        return request;
    }

    private UpdateNodeFileDTO formatRequest(@NotNull UpdateNodeFileDTO request){
        final int POS_DESIGN_STATUS = 1;
        final int POS_CHECK_STATUS = 2;
        final int POS_AUDIT_STATUS = 3;
        final int MIN_LENGTH = 8;

        request = BeanUtils.cleanProperties(request);
        if (request == null){
            return null;
        }
        if (StringUtils.isNotEmpty(request.getPassDesign()) ||
                StringUtils.isNotEmpty(request.getPassCheck()) ||
                StringUtils.isNotEmpty(request.getPassAudit())){
            StringBuilder statusBuilder = new StringBuilder(String.valueOf(StringUtils.getChar(request.getPassDesign(), 1, StringUtils.getChar(request.getStatus(), POS_DESIGN_STATUS, '0'))) +
                    StringUtils.getChar(request.getPassCheck(), 1, StringUtils.getChar(request.getStatus(), POS_CHECK_STATUS, '0')) +
                    StringUtils.getChar(request.getPassAudit(), 1, StringUtils.getChar(request.getStatus(), POS_AUDIT_STATUS, '0')));
            for (int i=POS_AUDIT_STATUS; i<MIN_LENGTH;i++){
                statusBuilder.append("0");
            }
            request.setStatus(statusBuilder.toString());
        }
        if (request.getHistoryUpdateRequest() != null){
            UpdateHistoryDTO createHistoryRequest = request.getHistoryUpdateRequest();
            BeanUtils.copyCleanProperties(request,createHistoryRequest);
            createHistoryRequest =  BeanUtils.cleanProperties(createHistoryRequest);
            request.setHistoryUpdateRequest(createHistoryRequest);
        }
        return request;
    }


    private UpdateNodeDTO updateRequest(@NotNull UpdateNodeDTO request,SimpleNodeDTO parent,StorageTreeEntity parentEntity,String nodeName){
        request.setNodeName(nodeName);
        if (EntityUtils.isValid(parentEntity)){
            request.setPid(parentEntity.getId());
            request.setPath(StringUtils.appendPath(parentEntity.getPath(),nodeName));
        } else {
            request.setPid("-");
            request.setPath(nodeName);
        }
        if (isValid(parent)){
            if (StringUtils.isEmpty(request.getTypeId())) {
                if (LocalConstService.isDirectoryType(request.getTypeId())) {
                    request.setTypeId(LocalConstService.getPathType(parent.getTypeId()));
                } else {
                    request.setTypeId(LocalConstService.getFileType(parent.getTypeId()));
                }
            }
            request.setProjectId(parent.getProjectId());
            request.setTaskId(parent.getTaskId());
        }
        return request;
    }

    private void createHistory(@NotNull UpdateHistoryDTO request, String fileId){
        if (StringUtils.isEmpty(request.getMainFileId())){
            request.setMainFileId(fileId);
        }
        EntityUtils.replace(storageFileHisDao,StorageFileHisEntity.class,request);
    }

    @Override
    public SimpleNodeDTO updateNode(@NotNull SimpleNodeDTO src, SimpleNodeDTO parent, @NotNull UpdateNodeDTO request, Current current) throws CustomException {
        CheckService.check(isValid(src),ErrorCode.DataIsInvalid);

        long t = System.currentTimeMillis();

        request = BeanUtils.cleanProperties(request);
        //填充路径
        if (StringUtils.isEmpty(request.getPath())){
            request.setPath(request.getNodeName());
        }

        String requestPath = request.getPath();

        //*//本行前为双斜杠，则不调用selectStorageEntityUnion
        //如果父节点被更改，则调整request
        if (isValid(parent) && StringUtils.isNotSame(src.getPid(),parent.getId())) {
            String nodeName = StringUtils.getFileName(request.getPath());
            if (StringUtils.isEmpty(nodeName)) {
                nodeName = src.getName();
            }
            CheckService.check(StringUtils.isNotEmpty(nodeName),ErrorCode.DataIsInvalid);
            StorageTreeEntity parentEntity = getParentEntity(parent, request, current);
            request = updateRequest(request,parent,parentEntity,nodeName);
            request.setNodeName(nodeName);
        }

        //更新数据库
        String id = StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH);
        StorageTreeEntity nodeEntity = EntityUtils.replace(storageTreeDao,StorageTreeEntity.class,formatRequest(request),id);
        CheckService.check(EntityUtils.isValid(nodeEntity),ErrorCode.DataIsInvalid);
        if (request.getUpdateFileRequest() != null) {
            UpdateNodeFileDTO fileUpdateRequest = request.getUpdateFileRequest();
            EntityUtils.replace(storageFileDao,StorageFileEntity.class,formatRequest(fileUpdateRequest),id);
            if (fileUpdateRequest.getHistoryUpdateRequest() != null){
                createHistory(fileUpdateRequest.getHistoryUpdateRequest(),nodeEntity.getId());
            }
        }

        //填充返回节点的文字信息
        if ((nodeEntity != null) && StringUtils.isSame("-",nodeEntity.getPid())){
            nodeEntity.setPid(null);
        }
        SimpleNodeDTO node = convertToSimpleNodeDTO(nodeEntity,src,parent,requestPath,request.getOwnerUserId());

        /*///维持已测试过的updateNode
        //获取要修改的节点信息
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH));
        StorageEntityUnionDTO srcUnion = storageDao.selectStorageEntityUnion(BeanUtils.cleanProperties(query));
        BeanUtils.copyCleanProperties(request,srcUnion);

        //获取父节点信息
        String nodeName = StringUtils.getFileName(request.getPath());
        if (StringUtils.isEmpty(nodeName)) {
            nodeName = srcUnion.getNodeName();
        }
        CheckService.check(StringUtils.isNotEmpty(nodeName),ErrorCode.DataIsInvalid);
        if (isValid(parent)) {
            StorageTreeEntity parentEntity = getParentEntity(parent, request, current);
            request = updateRequest(request,parent,parentEntity,nodeName);
        }
        request.setNodeName(nodeName);

        //更改属性
        StorageTreeEntity nodeEntity = EntityUtils.replace(storageTreeDao,StorageTreeEntity.class,formatRequest(request),srcUnion);
        CheckService.check(EntityUtils.isValid(nodeEntity),ErrorCode.DataIsInvalid);
        if (request.getUpdateFileRequest() != null) {
            UpdateNodeFileDTO fileUpdateRequest = request.getUpdateFileRequest();
            StorageFileEntity fileEntity = srcUnion.getFileEntity();
            EntityUtils.replace(storageFileDao,StorageFileEntity.class,formatRequest(fileUpdateRequest),fileEntity,nodeEntity.getId());
            if (fileUpdateRequest.getHistoryUpdateRequest() != null){
                createHistory(fileUpdateRequest.getHistoryUpdateRequest(),nodeEntity.getId());
            }
        }
        //填充返回节点的文字信息
        if ((srcUnion != null) && StringUtils.isSame("-",srcUnion.getPid())){
            srcUnion.setPid(null);
        }
        SimpleNodeDTO node = convertToSimpleNodeDTO(srcUnion,src,parent,requestPath,request.getOwnerUserId());
        //*/


        log.info("\t----> updateNode:" + (System.currentTimeMillis()-t) + "ms");
        return node;
    }

    private boolean isSameParent(@NotNull SimpleNodeDTO src, SimpleNodeDTO parent, UpdateNodeDTO request){
        StringBuilder pathBuilder = new StringBuilder();
        if (parent != null){
            pathBuilder.append(parent.getPath());
        } else {
            pathBuilder.append(StringUtils.getDirName(src.getPath()));
        }
        if ((request != null) && (!StringUtils.isEmpty(request.getPath()))){
            String dirName = StringUtils.getDirName(request.getPath());
            if (StringUtils.isNotEmpty(dirName)) {
                if (pathBuilder.length() > 0) pathBuilder.append(StringUtils.SPLIT_PATH);
                pathBuilder.append(dirName);
            }
        }
        return StringUtils.isSame(StringUtils.getDirName(src.getPath()),StringUtils.formatPath(pathBuilder.toString()));
    }

    @Override
    public void deleteNodeByIdList(@NotNull List<String> idList, DeleteAskDTO request, Current current) throws CustomException {
        long t = System.currentTimeMillis();

        if (ObjectUtils.isNotEmpty(idList)) {
            int n = 0;
            String userId = (request != null) ? request.getLastModifyUserId() : null;
            storageTreeDao.fakeDeleteById(idList, userId);
            storageFileDao.fakeDeleteById(idList, userId);
            storageFileHisDao.fakeDeleteById(idList, userId);
        }

        log.info("\t----> deleteNodeByIdList:" + (System.currentTimeMillis()-t) + "ms," + idList.size() + "个节点");
    }

    @Override
    public void deleteNodeById(@NotNull String id, DeleteAskDTO request, Current current) throws CustomException {
        List<String> idList = new ArrayList<>();
        id = StringUtils.left(id, StringUtils.DEFAULT_ID_LENGTH);
        if (StringUtils.isNotEmpty(id)) {
            idList.add(id);
        }
        deleteNodeByIdList(idList,request,current);
    }

    @Override
    public void deleteNodeList(@NotNull List<SimpleNodeDTO> nodeList, DeleteAskDTO request, Current current) throws CustomException {
        List<String> idList = new ArrayList<>();
        for (SimpleNodeDTO node : nodeList) {
            if (node.getIsDirectory()) {
                List<SimpleNodeDTO> children = listChildren(node, current);
                if (ObjectUtils.isNotEmpty(children)) {
                    for (SimpleNodeDTO child : children) {
                        idList.add(StringUtils.left(child.getId(), StringUtils.DEFAULT_ID_LENGTH));
                    }
                }
            }
            idList.add(StringUtils.left(node.getId(),StringUtils.DEFAULT_ID_LENGTH));
        }
        deleteNodeByIdList(idList,request,current);
    }

    @Override
    public void deleteNode(@NotNull SimpleNodeDTO node, DeleteAskDTO request, Current current) throws CustomException {
        if (StringUtils.isNotEmpty(node.getId())) {
            List<SimpleNodeDTO> list = new ArrayList<>();
            list.add(node);
            deleteNodeList(list, request, current);
        }
    }

    private boolean isRoot(@NotNull SimpleNodeDTO parent){
        return StringUtils.isEmpty(parent.getId());
    }

    @Override
    public List<SimpleNodeDTO> listChild(@NotNull SimpleNodeDTO parent, Current current) throws CustomException {
        if (isRoot(parent)) return listRoot(null,current);

        CheckService.check(parent.getIsDirectory(),ErrorCode.InvalidParameter,"listChild");
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid(parent.getId());
        return listNode(query,current);

    }

    @Override
    public List<SimpleNodeDTO> listChildren(@NotNull SimpleNodeDTO parent, Current current) throws CustomException {
        QueryNodeDTO nodeQuery = new QueryNodeDTO();
        if (!isRoot(parent)) {
            CheckService.check(parent.getIsDirectory(),ErrorCode.InvalidParameter,"节点不是目录");
            CheckService.check(StringUtils.isNotEmpty(parent.getPath()),ErrorCode.InvalidParameter,"节点路径为空");
            nodeQuery.setParentPath(parent.getPath());
        }
        return listNode(nodeQuery,current);
    }

    @Override
    public List<SimpleNodeDTO> listRoot(String accountId, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid("-");
        query.setAccountId(accountId);
        return listNode(query,current);
    }


    @Override
    public SimpleNodeDTO updateNodeSimple(@NotNull SimpleNodeDTO src, @NotNull UpdateNodeDTO request, Current current) throws CustomException {
        return updateNode(src,null,request,current);
    }

    private void updateStorageUnion(@NotNull StorageEntityUnionDTO srcUnion){
        if (srcUnion.getHisEntity() != null) {
            StorageFileHisEntity hisEntity = srcUnion.getHisEntity();
            storageFileHisDao.updateById(hisEntity);
        }
        if (srcUnion.getFileEntity() != null) {
            StorageFileEntity fileEntity = srcUnion.getFileEntity();
            fileEntity.setMainFileId(StringUtils.left(fileEntity.getMainFileId(),StringUtils.DEFAULT_ID_LENGTH));
            storageFileDao.updateById(fileEntity);
        }
        storageTreeDao.updateById(srcUnion);
    }

    private void insertStorageUnion(@NotNull StorageEntityUnionDTO srcUnion){
        if (srcUnion.getHisEntity() != null) {
            StorageFileHisEntity hisEntity = srcUnion.getHisEntity();
            storageFileHisDao.insert(BeanUtils.cleanProperties(hisEntity));
        }
        if (srcUnion.getFileEntity() != null) {
            StorageFileEntity fileEntity = srcUnion.getFileEntity();
            fileEntity.setMainFileId(StringUtils.left(fileEntity.getMainFileId(),StringUtils.DEFAULT_ID_LENGTH));
            storageFileDao.insert(BeanUtils.cleanProperties(fileEntity));
        }
        storageTreeDao.insert(BeanUtils.cleanProperties(srcUnion));
    }

    private SimpleNodeDTO getNodeByFuzzyPathOld(String fuzzyPath){
        SimpleNodeDTO node = null;
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyPath(fuzzyPath);
        List<SimpleNodeDTO> parentNodeList = storageDao.listNode(BeanUtils.cleanProperties(query));
        if ((parentNodeList != null) && (!parentNodeList.isEmpty())) {
            node = parentNodeList.get(0);
        }
        return node;
    }

    private SimpleNodeDTO getNodeByIdOld(String id){
        SimpleNodeDTO node = null;
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(id);
        List<SimpleNodeDTO> parentNodeList = storageDao.listNode(BeanUtils.cleanProperties(query));
        if ((parentNodeList != null) && (!parentNodeList.isEmpty())) {
            node = parentNodeList.get(0);
        }
        return node;
    }

    @Override
    public List<SimpleNodeDTO> listNode(@NotNull QueryNodeDTO query, Current current) {
        long t = TraceUtils.enter(log);

        query.setPath(StringUtils.formatPath(query.getPath()));
        query.setParentPath(StringUtils.formatPath(query.getParentPath()));
        query.setFuzzyPath(StringUtils.formatPath(query.getFuzzyPath()));

        List<SimpleNodeDTO> list = null;
        String key = JsonUtils.obj2CleanJson(query);
        log.info("StorageService.listNode：" + key);
        final Integer MAX_QUERY_USE_TIMES = -1;
        list = simpleNodeMap.get(key);
        Integer times = queryTimesMap.get(key);
        if ((list == null) || (times > MAX_QUERY_USE_TIMES)) {
            list = storageDao.listNode(BeanUtils.cleanProperties(query));
            simpleNodeMap.put(key, list);
            queryTimesMap.put(key,0);
        } else {
            times++;
            queryTimesMap.put(key,times);
        }

        TraceUtils.exit(log,t,ObjectUtils.getFirst(list));
        return list;
    }


    private boolean isOwner(AccountDTO account, StorageTreeEntity nodeEntity){
        assert (nodeEntity != null);
        return isOwner(account,nodeEntity.getLastModifyUserId());
    }

    private boolean isOwner(AccountDTO account, String ownerId){
        return (StringUtils.isEmpty(ownerId) ||
                ((account != null) && StringUtils.isSame(account.getId(),ownerId)));
    }


    private SimpleNodeDTO convertToSimpleNodeDTO(StorageTreeEntity entity,SimpleNodeDTO src,SimpleNodeDTO parent,String requestPath,String requestOwnerUserId){
        if (entity == null) return null;
        //获取路径
        StringBuilder pathBuilder = new StringBuilder();
        if (parent != null){
            pathBuilder.append(parent.getPath());
        } else if (src != null) {
            pathBuilder.append(StringUtils.getDirName(src.getPath()));
        }
        pathBuilder.append(StringUtils.SPLIT_PATH);
        if (StringUtils.isNotEmpty(requestPath)){
            pathBuilder.append(requestPath);
        } else {
            if (pathBuilder.length() > 0) {
                pathBuilder.append(entity.getNodeName());
            } else {
                pathBuilder.append(entity.getPath());
            }
        }
        String path = StringUtils.formatPath(pathBuilder.toString());
        if ((parent == null) && (src == null)) {
            try {
                return getNodeByPath(path,(Current)null);
            } catch (CustomException e) {
                log.warn("无法使用路径查找到记录");
            }
        }
        SimpleNodeDTO node = BeanUtils.createCleanFrom(entity,SimpleNodeDTO.class);
        if (StringUtils.isNotEmpty(entity.getTypeId())) {
            node.setIsDirectory(LocalConstService.isDirectoryType(entity.getTypeId()));
        } else {
            entity.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_UNKNOWN));
            node.setIsDirectory(false);
        }
        node.setName(entity.getNodeName());
        node.setPath(path);
        Short rangeId = DigitUtils.parseShort(LocalConstService.getRangeId(entity.getTypeId()));
        node.setId(node.getId() + "-" + rangeId);
        if (StringUtils.isEmpty(node.getPid())) {
            if (isValid(parent)){
                node.setPid(parent.getId());
            } else {
                String pid = (StringUtils.isNotEmpty(node.getTaskId()) ? node.getTaskId() : node.getProjectId())
                        + "-" + rangeId;
                node.setPid(pid);
            }
        } else {
            node.setPid(node.getPid() + "-" + rangeId);
        }
        if (entity.getCreateTime() != null) {
            node.setCreateTimeStamp(entity.getCreateTime().getTime());
        }
        if (entity.getLastModifyTime() != null) {
            node.setLastModifyTimeStamp(entity.getLastModifyTime().getTime());
        }
        if (parent != null){
            node.setProjectName(parent.getProjectName());
            node.setTaskName(parent.getTaskName());
        } else if (src != null){
            node.setProjectName(src.getProjectName());
            node.setTaskName(src.getTaskName());
        }
        if ((src != null) && (StringUtils.isEmpty(requestOwnerUserId) || StringUtils.isSame(src.getOwnerUserId(),requestOwnerUserId))) {
            node.setOwnerName(src.ownerName);
        }
        return node;
    }


    @Override
    public SimpleNodeDTO createNode(SimpleNodeDTO parent, @NotNull UpdateNodeDTO request, Current current) throws CustomException {

        long t = System.currentTimeMillis();

        if (StringUtils.isEmpty(request.getPath())){
            request.setPath(request.getNodeName());
        }

        //获取父节点信息
        if (!isValid(parent)){
            parent = getParent(request,current);
        }
        String requestPath = request.getPath();
        StorageTreeEntity parentEntity = getParentEntity(parent,request,current);

        //创建节点
        String nodeName = StringUtils.getFileName(request.getPath());
        CheckService.check(StringUtils.isNotEmpty(nodeName),ErrorCode.DataIsInvalid);

        request = updateRequest(request,parent,parentEntity,nodeName);

        //如果父节点为'-'，设置父节点为空
        if ((request != null) && (StringUtils.isSame("-",request.getPid()))){
            request.setPid(null);
        }

        //插入节点
        StorageTreeEntity nodeEntity = EntityUtils.replace(storageTreeDao,StorageTreeEntity.class,formatRequest(request));
        CheckService.check(EntityUtils.isValid(nodeEntity),ErrorCode.DataIsInvalid);
        if (request.getUpdateFileRequest() != null) {
            UpdateNodeFileDTO fileUpdateRequest = request.getUpdateFileRequest();
            EntityUtils.replace(storageFileDao,StorageFileEntity.class,formatRequest(fileUpdateRequest),nodeEntity.getId());
            if (fileUpdateRequest.getHistoryUpdateRequest() != null){
                createHistory(fileUpdateRequest.getHistoryUpdateRequest(),nodeEntity.getId());
            }
        }

        //填充返回节点的文字信息
        SimpleNodeDTO node = convertToSimpleNodeDTO(nodeEntity,null,parent,requestPath,request.getOwnerUserId());
        if (node != null) {
            clearBuffer(node.getPid(), null);
        }

        log.info("\t----> createNode:" + (System.currentTimeMillis()-t) + "ms");
        return node;
    }

    @Override
    public SimpleNodeDTO createNodeWithRequestOnly(@NotNull UpdateNodeDTO request, Current current) throws CustomException {
        return createNode(null,request,current);
    }

    private StorageTreeEntity getParentEntity(SimpleNodeDTO parent, @NotNull UpdateNodeDTO request, Current current) throws CustomException {
        StorageTreeEntity parentEntity = null;
        if (StringUtils.isNotEmpty(request.getPath())) {
            String parentPath = StringUtils.getDirName(request.getPath());
            if (StringUtils.isNotEmpty(parentPath)) {
                if (!StringUtils.isStartWith(parentPath, StringUtils.SPLIT_PATH)) {
                    parentPath = StringUtils.SPLIT_PATH + parentPath;
                }
            }
            QueryNodeDTO query = new QueryNodeDTO();
            if (isValid(parent)) {
                parentPath = parent.getPath() + parentPath;
                query.setProjectId(parent.getProjectId());
                query.setTaskId(parent.getTaskId());
            }
            query.setFuzzyPath(parentPath);
            query.setOnlyOne(LocalConstService.MODE_TRUE);
            List<SimpleNodeDTO> direList = listNode(query,current);
            SimpleNodeDTO dirNode = ObjectUtils.getFirst(direList);
            if (isValid(dirNode)) {
                String path = StringUtils.substring(parentPath, StringUtils.length(dirNode.getPath()) + StringUtils.SPLIT_PATH.length());
                path = StringUtils.appendPath(path,StringUtils.getFileName(request.getPath()));
                request.setPath(path);
            }
            String dirName = StringUtils.getDirName(request.getPath());
            if (StringUtils.isNotEmpty(dirName)) {
                parentEntity = insertPathNodeList(parent, dirName, request.getLastModifyUserId(), request.getLastModifyRoleId());
            } else if (isValid(parent)){
                parentEntity = storageTreeDao.selectById(StringUtils.left(parent.getId(),StringUtils.DEFAULT_ID_LENGTH));
            }
        }
        return parentEntity;
    }

    private SimpleNodeDTO getParent(@NotNull UpdateNodeDTO request, Current current) throws CustomException {
        SimpleNodeDTO parent = null;
        if (StringUtils.isNotEmpty(request.getPath())) {
            String parentPath = StringUtils.getDirName(request.getPath());
            if (StringUtils.isNotEmpty(parentPath)) {
                if (!StringUtils.isStartWith(parentPath, StringUtils.SPLIT_PATH)) {
                    parentPath = StringUtils.SPLIT_PATH + parentPath;
                }
            }
            QueryNodeDTO query = new QueryNodeDTO();
            query.setFuzzyPath(parentPath);
            query.setOnlyOne(LocalConstService.MODE_TRUE);
            List<SimpleNodeDTO> parentList = listNode(query, current);
            parent = ObjectUtils.getFirst(parentList);
            if (isValid(parent)) {
                String path = StringUtils.substring(parentPath, StringUtils.length(parent.getPath()) + StringUtils.SPLIT_PATH.length());
                path = StringUtils.appendPath(path,StringUtils.getFileName(request.getPath()));
                request.setPath(path);
            }
        }
        return parent;
    }

    @Override
    public SimpleNodeDTO getNodeById(String id, Current current) throws CustomException {
        if (StringUtils.isEmpty(id)) return null;
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(id);
        query.setNeedPath(LocalConstService.MODE_TRUE);
        List<SimpleNodeDTO> list = listNode(query, current);
        return ObjectUtils.getFirst(list);
    }

    @Override
    public SimpleNodeDTO getNodeByPath(String path, Current current) throws CustomException {
        if (StringUtils.isEmpty(path)) return null;
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPath(StringUtils.formatPath(path));
        List<SimpleNodeDTO> list = listNode(query, current);
        return ObjectUtils.getFirst(list);
    }

    @Override
    public SimpleNodeDTO getNodeByFuzzyPath(String fuzzyPath, Current current) throws CustomException {
        if (StringUtils.isEmpty(fuzzyPath)) return null;
        if (!StringUtils.isStartWith(fuzzyPath, StringUtils.SPLIT_PATH)) {
            fuzzyPath = StringUtils.SPLIT_PATH + fuzzyPath;
        }
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyPath(StringUtils.formatPath(fuzzyPath));
        List<SimpleNodeDTO> list = listNode(query, current);
        return ObjectUtils.getFirst(list);
    }
}
