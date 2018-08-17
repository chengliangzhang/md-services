package com.maoding.fileServer;

import com.maoding.common.CheckService;
import com.maoding.common.LocalConstService;
import com.maoding.common.config.IceConfig;
import com.maoding.common.config.WebServiceConfig;
import com.maoding.common.zeroc.*;
import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreException.WebException;
import com.maoding.coreFileServer.CoreCreateFileRequest;
import com.maoding.coreFileServer.CoreFileDataDTO;
import com.maoding.coreFileServer.CoreFileServer;
import com.maoding.coreFileServer.disk.DiskFileServer;
import com.maoding.coreFileServer.web.WebFileServer;
import com.maoding.coreUtils.*;
import com.maoding.fileServer.config.FileServerConfig;
import com.maoding.fileServer.dto.CopyRequestDTO;
import com.maoding.fileServer.zeroc.*;
import com.maoding.notice.zeroc.NoticeClientPrx;
import com.maoding.notice.zeroc.NoticeServicePrx;
import com.maoding.storage.zeroc.*;
import com.maoding.user.zeroc.*;
import com.zeroc.Ice.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.*;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/25 10:08
 * 描    述 :
 */
@SuppressWarnings("deprecation")
@Service("fileService")
public class FileServiceImpl extends CoreLocalService implements FileService {

    @Autowired
    private IceConfig iceConfig;

    @Autowired
    private FileServerConfig fileServerConfig;

    @Autowired
    private WebServiceConfig webServiceConfig;

    private CoreFileServer fileServer = new DiskFileServer();

    private static final int NODE_IGNORE_TIME = 3 * 1000;
    private static final int FILE_IGNORE_TIME = 100 * 1000;
    private static final int MAX_BUFFER_SIZE = 10;

    //读缓存
    private Map<String,List<SimpleNodeDTO>> nodeListMap = new HashMap<>();
    private Map<String,Long> nodeIgnoreMap = new HashMap<>();
    private Map<String,List<SimpleNodeDTO>> skyNodeListMap = new HashMap<>();
    private Map<String,Long> skyNodeIgnoreMap = new HashMap<>();
    private Map<String,List<NodeFileDTO>> fileListMap = new HashMap<>();
    private Map<String,Long> fileIgnoreMap = new HashMap<>();
    private Map<String,List<FullNodeDTO>> fullNodeListMap = new HashMap<>();
    private Map<String,Long> fullNodeIgnoreMap = new HashMap<>();

    //写缓存
    private volatile Map<String,UpdateNodeDTO> nodeUpdateMap = new HashMap<>();
    private volatile Map<String,SimpleNodeDTO> nodeUpdateParentMap = new HashMap<>();
    private volatile Map<String,UpdateNodeFileDTO> fileUpdateMap = new HashMap<>();

    //使用的节点服务器
    private StorageServicePrx storageServicePrx = null;

    private AccountDTO lastAccount = null;


    @Override
    public long getVersionLength(VersionDTO version, Current current) throws CustomException {
        return getCommonService().getVersionLength(version,current);
    }

    @Override
    public FileDataDTO readVersion(VersionDTO version, long pos, int size, Current current) throws CustomException {
        return getCommonService().readVersion(version,pos,size,current);
    }

    @Override
    public long getFileLength(@NotNull NodeFileDTO file, boolean readOnly, Current current) throws CustomException {
        long t = TraceUtils.enter(log,file);

        long len = 0;
        String key = (readOnly) ? file.getReadOnlyKey() : file.getWritableKey();
        if (StringUtils.isNotEmpty(key)) {
            CoreFileServer coreService = getCoreServiceByFile(file, current);
            len = coreService.coreGetFileLength(key);
        }

        TraceUtils.exit(log,t);
        return len;
    }

    private CoreFileServer getCoreServiceByFile(@NotNull NodeFileDTO file, Current current) throws CustomException {
        return getCoreFileServer(file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
    }

    @Override
    public void setStorageService(StorageServicePrx storageService, Current current) {
        storageServicePrx = storageService;
    }

    private StorageServicePrx getStorageService(Current current) throws CustomException {
        StorageServicePrx prx = storageServicePrx;
        if (prx == null){
            prx = getCommonService().getDefaultStorageService(current);
        }
        return prx;
    }

    private CommonService getCommonService() {
        return SpringUtils.getBean(CommonService.class);
    }

    @Override
    public VersionDTO getNewestClient(Current current) throws CustomException {
        return getCommonService().getNewestClient(current);
    }

    @Override
    public SummaryFileDTO summaryFile(@NotNull QuerySummarySimpleDTO query, Current current) throws CustomException {
        QuerySummaryDTO storageQuery = BeanUtils.createCleanFrom(query,QuerySummaryDTO.class);
        storageQuery.setNotServerTypeId(Short.toString(LocalConstService.FILE_SERVER_TYPE_WEB));
        return getStoragePrx().summaryFile(storageQuery);
    }

    @Override
    public void clearAll(AccountDTO account, Current current) throws CustomException {
        CoreFileServer localServer = getCoreFileServer();
        List<String> keyList = localServer.coreListKey();
        if (ObjectUtils.isNotEmpty(keyList)) {
            for (String key : keyList) {
                clearKey(account, key, current);
            }
        }
    }

    @Override
    public void clearKey(AccountDTO account, @NotNull String key, Current current) throws CustomException {
        //根据实体文件查找文件节点
        QueryNodeDTO query = new QueryNodeDTO();
        query.setKey(key);
        List<NodeFileDTO> fileList = callStorageListFile(query,getAccountId(account));
        if (ObjectUtils.isNotEmpty(fileList)){
            //如果找到，根据文件节点路径更新实体文件路径
            for (NodeFileDTO file : fileList) {
                formatKey(account,file,current);
            }
        } else {
            //如果没找到，删除实体文件
            CoreFileServer localServer = getCoreFileServer();
            localServer.coreDeleteFile(key);
        }
    }

    private boolean isLike(@NotNull String nodePath, @NotNull String key){
        if (StringUtils.isSame(nodePath,key)){
            return true;
        } else {
            String nodeDir = StringUtils.getDirName(nodePath);
            String keyDir = StringUtils.getDirName(key);
            String nodeFile = StringUtils.getFileNameWithoutExt(nodePath);
            String keyFile = StringUtils.getFileNameWithoutExt(key);
            keyFile = StringUtils.left(keyFile,StringUtils.length(nodeFile));
            String nodeExt = StringUtils.getFileExt(nodePath);
            String keyExt = StringUtils.getFileExt(key);
            return (StringUtils.isSame(nodeDir,keyDir)) &&
                    (StringUtils.isSame(nodeFile,keyFile)) &&
                    (StringUtils.isSame(nodeExt,keyExt));
        }
    }

    private String getFormattedKey(AccountDTO account, @NotNull String key, @NotNull String path, Current current){
        String formattedKey = null;
        if (!isLike(path,key)){
            CoreFileServer localServer = getCoreFileServer();
            File localFile = localServer.coreGetFile(key);
            formattedKey = localServer.coreCreateFile(path,localFile);
        }
        return formattedKey;
    }

    private void formatKey(AccountDTO account, @NotNull NodeFileDTO file, Current current) throws CustomException {
        SimpleNodeDTO node = getFirstNodeByFile(account,file,current);
        if (isValid(node)){
            String path = getNodePathForAccount(account,node,current);
            String readOnlyKey = null;
            String writableKey = null;
            if (StringUtils.isNotEmpty(file.getReadOnlyKey())) {
                readOnlyKey = getFormattedKey(account,file.getReadOnlyKey(),path,current);
            }
            if (StringUtils.isNotEmpty(file.getWritableKey())) {
                writableKey = getFormattedKey(account,file.getWritableKey(),path,current);
            }
            if ((StringUtils.isNotEmpty(readOnlyKey)) || (StringUtils.isNotEmpty(writableKey))){
                UpdateNodeFileDTO updateFile = new UpdateNodeFileDTO();
                updateFile.setReadOnlyKey(readOnlyKey);
                updateFile.setWritableKey(writableKey);

                callStorageUpdateFile(file,updateFile,getAccountId(account));
            }
        }
    }

    private SimpleNodeDTO getFirstNodeByFile(AccountDTO account,NodeFileDTO file,Current current) throws CustomException {
        List<SimpleNodeDTO> nodeList = listNodeByFile(account,file,current);
        return ObjectUtils.getFirst(nodeList);
    }

    @Override
    public List<CANodeDTO> listDesignNode(AccountDTO account, Current current) throws CustomException {
        log.info("\t===>>> 进入 listDesignNode:" + JsonUtils.obj2CleanJson(account));
        long t0 = System.currentTimeMillis();
        AskFileDTO query = new AskFileDTO();
        query.setDesignMode(LocalConstService.MODE_TRUE);
        query.setHistoryMode(LocalConstService.MODE_FALSE);
        query.setIsTaskDesigner(LocalConstService.MODE_TRUE);
        query.setIsTaskChecker(LocalConstService.MODE_FALSE);
        query.setIsTaskAuditor(LocalConstService.MODE_FALSE);
        query.setAccountId(getAccountId(account));
        List<NodeFileDTO> fileList = listFile(account,query,current);
        List<CANodeDTO> caList = BeanUtils.createCleanListFrom(fileList,CANodeDTO.class);
        log.info("\t===>>> 退出 listDesignNode:"+ (System.currentTimeMillis()-t0) + "ms," + JsonUtils.obj2CleanJson(ObjectUtils.getFirst(caList)));
        return caList;
    }

    @Override
    public List<CANodeDTO> listCANode(AccountDTO account, Current current) throws CustomException {
        log.info("\t===>>> 进入 listCANode:" + JsonUtils.obj2CleanJson(account));
        long t0 = System.currentTimeMillis();
        AskFileDTO query = new AskFileDTO();
        query.setCaMode(LocalConstService.MODE_TRUE);
        query.setHistoryMode(LocalConstService.MODE_FALSE);
        query.setIsTaskDesigner(LocalConstService.MODE_FALSE);
        query.setIsTaskChecker(LocalConstService.MODE_TRUE);
        query.setIsTaskAuditor(LocalConstService.MODE_TRUE);
        query.setAccountId(getAccountId(account));
        List<NodeFileDTO> fileList = listFile(account,query,current);
        List<CANodeDTO> caList = getCaNodeListByFileList(account,fileList,current);
        log.info("\t===>>> 退出 listCANode:"+ (System.currentTimeMillis()-t0) + "ms," + JsonUtils.obj2CleanJson(ObjectUtils.getFirst(caList)));
        return caList;
    }



    @Override
    public long getTime(Current current) throws CustomException {
        return (new Date()).getTime();
    }

    @Override
    public void restartWebRole(WebRoleDTO webRole, Current current) throws CustomException {
        setWebRoleStatus(webRole,"0",current);
    }

    @Override
    public void finishWebRole(WebRoleDTO webRole, Current current) throws CustomException {
        setWebRoleStatus(webRole,"1",current);
    }

    @Override
    public void setWebRoleStatus(WebRoleDTO webRole, String statusId, Current current) throws CustomException {
        getUserService().setWebRoleStatus(webRole,statusId);
    }

    @Override
    public WebRoleDTO getWebRole(@NotNull AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        QueryWebRoleDTO query = new QueryWebRoleDTO();
        query.setUserId(getAccountId(account));
        query.setTaskId(node.getTaskId());
        List<WebRoleDTO> list = getUserService().listWebRole(query);
        return (ObjectUtils.isNotEmpty(list)) ? list.get(0) : null;
    }

    @Override
    public List<WebRoleDTO> listWebRoleTask(AccountDTO account, Current current) throws CustomException {
        final String ROLE_TYPE_ID_FILETER = "40,41,42,43";
        QueryWebRoleDTO query = new QueryWebRoleDTO();
        query.setRoleId(ROLE_TYPE_ID_FILETER);
        query.setUserId(getAccountId(account));
        return getUserService().listWebRole(query);
    }

    @Override
    public List<WebRoleDTO> listAllWebRole(AccountDTO account, Current current) throws CustomException {
        final String ROLE_TYPE_ID_FILETER = "40,41,42,43";
        QueryWebRoleDTO query = new QueryWebRoleDTO();
        query.setRoleId(ROLE_TYPE_ID_FILETER);
        query.setAccountId(getAccountId(account));
        return getUserService().listWebRole(query);
    }

    @Override
    public boolean isExist(String path, Current current) throws CustomException {
        return isExistForAccount(null,path,current);
    }

    @Override
    public boolean isExistForAccount(AccountDTO account, String path, Current current) throws CustomException {
        SimpleNodeDTO node = getNodeByPathForAccount(account,path,current);
        return (node != null);
    }

    private String getNodeId(SimpleNodeDTO node) {
        return (node != null) ? node.getId() : null;
    }

    private String getNameByContent(String content) {
        String name = StringUtils.left(content,"\n");
        if (StringUtils.isNotEmpty(name)){
            name = StringUtils.left(name,255);
        }
        return name;
    }

    private String getNotNullString(String s) {
        return (s != null) ? s : "";
    }

    @Override
    public AnnotateDTO createAnnotateCheck(AccountDTO account, @NotNull SimpleNodeDTO node, @NotNull AnnotateRequestDTO request, Current current) throws CustomException {
        request.setTypeId(Short.toString(LocalConstService.ANNOTATE_TYPE_CHECK));
        return createAnnotate(account,node,request,current);
    }

    @Override
    public AnnotateDTO createAnnotateAudit(AccountDTO account, @NotNull SimpleNodeDTO node, @NotNull AnnotateRequestDTO request, Current current) throws CustomException {
        request.setTypeId(Short.toString(LocalConstService.ANNOTATE_TYPE_AUDIT));
        return createAnnotate(account,node,request,current);
    }

    @Override
    public AnnotateDTO createAnnotate(AccountDTO account, @NotNull SimpleNodeDTO node, AnnotateRequestDTO request, Current current) throws CustomException {
        //获取文件
        CheckService.check(!node.getIsDirectory(),ErrorCode.InvalidParameter,"createAnnotate");
        NodeFileDTO file = getFileInfoForAccount(account,node,current);
        CheckService.check(file != null,ErrorCode.DataIsInvalid,"createAnnotate");

        //建立创建申请
        UpdateAnnotateDTO updateRequest = BeanUtils.createCleanFrom(request,UpdateAnnotateDTO.class);
        updateRequest.setStatusId(request.getIsPassed() ? Short.toString(LocalConstService.ANNOTATE_STATUS_TYPE_PASS) : Short.toString(LocalConstService.ANNOTATE_STATUS_TYPE_REFUSE));
        updateRequest.setLastModifyUserId(getAccountId(account));

        EmbedElementDTO element = null;
        if (ObjectUtils.isNotEmpty(request.getData())){
            UpdateElementDTO elementRequest = BeanUtils.createCleanFrom(request,UpdateElementDTO.class);
            elementRequest.setDataArray(request.getData());
            element = getStoragePrx().createEmbedElement(elementRequest);
            if (element != null) {
                List<String> elementIdList = new ArrayList<>();
                elementIdList.add(element.getId());
                updateRequest.setAddElementIdList(elementIdList);
            }
        }
        if (ObjectUtils.isNotEmpty(request.getAddAccessoryList())) {
            List<String> fileIdList = new ArrayList<>();
            for (NodeFileDTO accessory : request.getAddAccessoryList()){
                fileIdList.add(accessory.getId());
            }
            updateRequest.setAddFileIdList(fileIdList);
        }
        AnnotateDTO annotate = getStoragePrx().createAnnotate(file,updateRequest);
        annotate.setElement(element);
        annotate.setAccessoryList(request.getAddAccessoryList());
        return annotate;
    }

    @Override
    public AnnotateDTO updateAnnotate(AccountDTO account, @NotNull AnnotateDTO annotate, @NotNull AnnotateRequestDTO request, Current current) throws CustomException {
        //建立更新申请
        UpdateAnnotateDTO updateRequest = BeanUtils.createCleanFrom(request,UpdateAnnotateDTO.class);
        updateRequest.setStatusId(request.getIsPassed() ? Short.toString(LocalConstService.ANNOTATE_STATUS_TYPE_PASS) : Short.toString(LocalConstService.ANNOTATE_STATUS_TYPE_REFUSE));
        updateRequest.setLastModifyUserId(getAccountId(account));

        EmbedElementDTO element = annotate.getElement();
        if (ObjectUtils.isNotEmpty(request.getData())){
            UpdateElementDTO elementRequest = BeanUtils.createCleanFrom(updateRequest,UpdateElementDTO.class);
            elementRequest.setDataArray(request.getData());

            if ((element != null) && (StringUtils.isNotEmpty(element.getId()))) {
                element = getStoragePrx().updateEmbedElement(element,elementRequest);
            } else {
                element = getStoragePrx().createEmbedElement(elementRequest);
                if (element != null) {
                    List<String> elementIdList = new ArrayList<>();
                    elementIdList.add(element.getId());
                    updateRequest.setAddElementIdList(elementIdList);
                }
            }
        }

        List<NodeFileDTO> fileList = annotate.getAccessoryList();
        if (ObjectUtils.isNotEmpty(request.getDelAccessoryList())) {
            List<String> fileIdList = new ArrayList<>();
            for (NodeFileDTO accessory : request.getDelAccessoryList()){
                fileIdList.add(accessory.getId());
                if (fileList != null) {
                    for (NodeFileDTO annotateAccessory : fileList) {
                        if (StringUtils.isSame(annotateAccessory.getId(), accessory.getId())) {
                            fileList.remove(annotateAccessory);
                            break;
                        }
                    }
                }
            }
            updateRequest.setDelAttachmentIdList(fileIdList);
        }
        if (ObjectUtils.isNotEmpty(request.getAddAccessoryList())) {
            List<String> fileIdList = new ArrayList<>();
            if (fileList == null) {
                fileList = new ArrayList<>();
            }
            for (NodeFileDTO accessory : request.getAddAccessoryList()){
                fileIdList.add(accessory.getId());
                fileList.add(accessory);
            }
            updateRequest.setAddFileIdList(fileIdList);
        }
        annotate = getStoragePrx().updateAnnotate(annotate,updateRequest);
        annotate.setElement(element);
        annotate.setAccessoryList(fileList);
        return annotate;
    }

    @Override
    public List<AnnotateDTO> listAnnotate(AccountDTO account, @NotNull QueryAnnotateDTO query, Current current) throws CustomException {
        return getStoragePrx().listAnnotate(query);
    }

    @Override
    public NodeFileDTO addAccessory(AccountDTO account, @NotNull AnnotateDTO annotate, @NotNull AccessoryRequestDTO request, Current current) throws CustomException {
        //创建文件
        UpdateNodeFileDTO storageRequest = new UpdateNodeFileDTO();
        storageRequest.setServerTypeId(fileServerConfig.getServerTypeId());
        storageRequest.setServerAddress(fileServerConfig.getServerAddress());
        storageRequest.setBaseDir(fileServerConfig.getBaseDir());
        if (ObjectUtils.isNotEmpty(request.getData())){
            CoreCreateFileRequest coreRequest = null;
            if (StringUtils.isNotEmpty(request.getPath())){
                coreRequest = new CoreCreateFileRequest();
                coreRequest.setPath(request.getPath());
            }
            String key = createLocalKey(coreRequest);
            CoreFileDataDTO data = new CoreFileDataDTO();
            data.setData(request.getData());
            getCoreFileServer().coreWriteFile(data,key);
            storageRequest.setReadOnlyKey(key);
        }
        storageRequest.setLastModifyUserId(getAccountId(account));
        NodeFileDTO file = callStorageCreateFile(storageRequest,getAccountId(account));

        //添加附件
        if ((file != null) && (StringUtils.isNotEmpty(file.getId()))) {
            List<String> fileIdList = new ArrayList<>();
            fileIdList.add(file.getId());
            UpdateAnnotateDTO addRequest = new UpdateAnnotateDTO();
            addRequest.setAddFileIdList(fileIdList);
            getStoragePrx().updateAnnotate(annotate,addRequest);
        }
        return file;
    }

    @Override
    public void deleteAccessory(AccountDTO account, @NotNull AnnotateDTO annotate, @NotNull NodeFileDTO accessory, Current current) throws CustomException {
        List<NodeFileDTO> fileList = annotate.getAccessoryList();
        if (fileList != null){
            for (NodeFileDTO file : fileList) {
                if (StringUtils.isSame(file.getId(),accessory.getId())){
                    List<String> fileIdList = new ArrayList<>();
                    fileIdList.add(file.getId());
                    UpdateAnnotateDTO delRequest = new UpdateAnnotateDTO();
                    delRequest.setDelAttachmentIdList(fileIdList);
                    getStoragePrx().updateAnnotate(annotate,delRequest);
                    fileList.remove(file);
                    break;
                }
            }
        }
    }


    private int writeRWFile(AccountDTO account, @NotNull NodeFileDTO file, @NotNull FileDataDTO data, String path, boolean isReadOnly, Current current) throws CustomException {
        String key = getFileKey(file,isReadOnly);
        //为测试而做的临时性解决方案，可以写入只读文件
        if (StringUtils.isEmpty(key)){
            key = getFileKey(file,!isReadOnly);
        }
        if (StringUtils.isEmpty(key)){
            UpdateNodeFileDTO updateRequest = createUpdateRequestForLocalKey(file,path,isReadOnly);
            CheckService.check(updateRequest != null,ErrorCode.DataIsInvalid,"writeRWFile");
            updateRequest.setLastModifyUserId(getAccountId(account));
            file = callStorageUpdateFile(file,updateRequest,getAccountId(account));
            CheckService.check((file != null) && (StringUtils.isNotEmpty(file.getId())),ErrorCode.DataIsInvalid,"writeRWFile");
            key = getFileKey(file,isReadOnly);
        }
        CoreFileDataDTO coreData = BeanUtils.createFrom(data,CoreFileDataDTO.class);
        return getCoreFileServer().coreWriteFile(coreData,key);
    }

    @Override
    public int writeAccessory(AccountDTO account, @NotNull NodeFileDTO file, FileDataDTO data, Current current) throws CustomException {
        return writeRWFile(account,file,data,null,true,current);
    }

    @Override
    public int writeFile(AccountDTO account, @NotNull NodeFileDTO file, @NotNull FileDataDTO data, String path, Current current) throws CustomException {
        return writeRWFile(account,file,data,path,false,current);
    }

    @Override
    public int writeFileAndRelease(AccountDTO account, @NotNull NodeFileDTO file, FileDataDTO data, String path, long fileLength, Current current) throws CustomException {
        int n = writeFile(account,file,data,path,current);
        releaseFile(account,file,path,current);
        return n;
    }

    @Override
    public void releaseFile(AccountDTO account, @NotNull NodeFileDTO file, String path, Current current) throws CustomException {
        String key = getFileKey(file,false);
        if (StringUtils.isNotEmpty(key)) {
            CoreFileServer localServer = getCoreFileServer();
            String md5 = localServer.coreCalcMd5(key);
            if (StringUtils.isNotSame(file.getFileMd5(),md5)){
                File localFile = localServer.coreGetFile(key);
                String localKey = createRealFile(fileServer,path,localFile);
                UpdateNodeFileDTO updateRequest = new UpdateNodeFileDTO();
                updateRequest.setReadOnlyKey(localKey);
                updateRequest.setFileLength(FileUtils.getFileLengthStr(localFile));
                updateRequest.setFileMd5(md5);
                NodeFileDTO localFileNode = getFileOnServer(file,getAccountId(account));
                callStorageUpdateFile(localFileNode,updateRequest,getAccountId(account));
                if (!isLocalFile(file))
                {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                CoreFileServer fileServer = getCoreFileServer(file.getServerTypeId(), file.getServerAddress(), file.getBaseDir());
                                String remoteReadOnlyKey = createRealFile(fileServer, path, localFile);
                                String remoteWritableKey = createRealFile(fileServer, path, localFile);
                                UpdateNodeFileDTO remoteUpdate = new UpdateNodeFileDTO();
                                remoteUpdate.setReadOnlyKey(remoteReadOnlyKey);
                                remoteUpdate.setWritableKey(remoteWritableKey);
                                callStorageUpdateFile(file, remoteUpdate,getAccountId(account));
                            } catch (CustomException e) {
                                log.error("发布文件时出错", e);
                            }
                        }
                    }.start();
                }
            }
        }
    }

    private NodeFileDTO getFileOnServer(@NotNull NodeFileDTO file, String accountId, String serverTypeId, String serverAddress, String baseDir) throws CustomException{
        CoreFileServer fileServer = getCoreFileServer(file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
        CoreFileServer dstServer = getCoreFileServer(serverTypeId,serverAddress,baseDir);
        if (isSameServer(fileServer,dstServer)){
            return file;
        } else {
            QueryNodeDTO query = new QueryNodeDTO();
            query.setFileId(file.getId());
            query.setServerTypeId(fileServerConfig.getServerTypeId(serverTypeId));
            query.setServerAddress(fileServerConfig.getServerAddress(serverTypeId, serverAddress));
            query.setBaseDir(fileServerConfig.getBaseDir(serverTypeId, baseDir));
            query.setOnlyOne(LocalConstService.MODE_TRUE);
            List<NodeFileDTO> fileList = callStorageListFile(query, accountId);
            return ObjectUtils.getFirst(fileList);
        }
    }

    private NodeFileDTO getFileOnServer(@NotNull NodeFileDTO file, String accountId) throws CustomException {
        return getFileOnServer(file,accountId,fileServerConfig.getServerTypeId(),fileServerConfig.getServerAddress(),fileServerConfig.getBaseDir());
    }

    @Override
    public void reloadFile(AccountDTO account, @NotNull NodeFileDTO file, String path, Current current) throws CustomException {
        String key = getFileKey(file,true);
        CheckService.check(StringUtils.isNotEmpty(key),ErrorCode.InvalidParameter,"reloadFile");

        CoreFileServer fileServer = getCoreFileServer(file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
        CoreFileServer localServer = getCoreFileServer();
        File localFile = fileServer.coreGetFile(key);
        String localKey = createRealFile(localServer,path,localFile);
        UpdateNodeFileDTO updateRequest = new UpdateNodeFileDTO();
        updateRequest.setWritableKey(localKey);
        NodeFileDTO localFileNode = getFileOnServer(file,getAccountId(account));
        callStorageUpdateFile(localFileNode,updateRequest,getAccountId(account));
        if (!isLocalFile(file)){
            new Thread() {
                @Override
                public void run() {
                    try {
                        String remoteWritableKey = createRealFile(fileServer,path,localFile);
                        UpdateNodeFileDTO remoteUpdate = new UpdateNodeFileDTO();
                        remoteUpdate.setWritableKey(remoteWritableKey);
                        NodeFileDTO remoteFileNode = getFileOnServer(file,getAccountId(account),file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
                        callStorageUpdateFile(remoteFileNode,remoteUpdate,getAccountId(account));

                        String localReadOnlyKey = createRealFile(localServer,path,localFile);
                        UpdateNodeFileDTO localUpdate = new UpdateNodeFileDTO();
                        localUpdate.setReadOnlyKey(localReadOnlyKey);
                        callStorageUpdateFile(localFileNode,localUpdate,getAccountId(account));
                    } catch (CustomException e) {
                        log.error("还原文件时出错",e);
                    }
                }
            }.start();
        }
    }


    private boolean isLocalFile(@NotNull NodeFileDTO file){
        return fileServerConfig.isLocalServer(file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
    }
    private String getFileKey(NodeFileDTO file, boolean isReadOnly){
        if (file == null) {
            return null;
        } else if (isReadOnly){
            return file.getReadOnlyKey();
//            return (isLocalFile(file)) ? file.getReadOnlyKey() : file.getReadOnlyMirrorKey();
        } else {
            return file.getWritableKey();
//            return (isLocalFile(file)) ? file.getWritableKey() : file.getWritableMirrorKey();
        }
    }
    private String getFileKey(@NotNull FullNodeDTO file, boolean isReadOnly){
        return getFileKey(file.getFileInfo(),isReadOnly);
    }
    private String getFileKey(@NotNull FullNodeDTO file){
        SimpleNodeDTO nodeInfo = file.getBasic();
        assert (nodeInfo != null) && !(nodeInfo.getIsDirectory());
        return getFileKey(file,nodeInfo.getIsReadOnly());
    }
    private String createLocalKey(@NotNull CoreCreateFileRequest request) throws CustomException {
        CoreFileServer server = getCoreFileServer();
        assert (server != null);
        return createRealFile(server,request);
    }
    private File getSrcFile(String serverTypeId,String serverAddress,String baseDir,String key){
        File file = null;
        if (StringUtils.isNotEmpty(key)){
            CoreFileServer server = getCoreFileServer(serverTypeId, serverAddress, baseDir);
            assert (server != null);
            file = server.coreGetFile(key);
        }
        return file;
    }
    private UpdateNodeDTO updateKey(@NotNull UpdateNodeDTO updateRequest, String key, boolean isReadOnly, boolean isMirror){
        if (isReadOnly && !isMirror) {
            updateRequest.setReadOnlyKey(key);
        } else if (!isReadOnly && !isMirror) {
            updateRequest.setWritableKey(key);
        } else if (isReadOnly) {
            updateRequest.setReadOnlyMirrorKey(key);
        } else {
            updateRequest.setWritableMirrorKey(key);
        }
        return updateRequest;
    }

    /**
     * 根据文件信息创建本地文件或本地镜像文件，并返回文件信息更改申请
     * @param fileInfo 当前的文件节点信息
     * @param path 要创建的本地文件的路径，如果存在同名文件，需要为新建文件添加时间戳，如果中间路径不存在，需要创建中间路径
     * @param fileLength 要创建的本地文件的长度，可以为0
     * @param isReadOnly 需要创建用于只读还是读写的文件，根据这个输入决定更改只读文件属性还是可写文件属性
     * @return 用于更改节点属性的对象
     */
    private UpdateNodeDTO createUpdateRequestForLocalKey(NodeFileDTO fileInfo,String path,long fileLength,boolean isReadOnly) throws CustomException{
        CoreCreateFileRequest coreCreateRequest = new CoreCreateFileRequest();
        coreCreateRequest.setPath(path);
        if (fileLength > 0) {
            coreCreateRequest.setFileLength(fileLength);
        }
        UpdateNodeDTO updateRequest = new UpdateNodeDTO();
        if (fileInfo == null){ //如果文件信息为空，则创建本地文件
            String key = createLocalKey(coreCreateRequest);
            updateRequest = updateKey(updateRequest,key,isReadOnly,false);
            updateRequest.setServerTypeId(fileServerConfig.getServerTypeId());
            updateRequest.setServerAddress(fileServerConfig.getServerAddress());
            updateRequest.setBaseDir(fileServerConfig.getBaseDir());
        } else if (isLocalFile(fileInfo)) { //如果是本地文件，则从另一侧复制,或创建本地文件
            String otherKey = (!isReadOnly) ? fileInfo.getReadOnlyKey() : fileInfo.getWritableKey();
            if (StringUtils.isNotEmpty(otherKey)) {
                File srcFile = getSrcFile(fileInfo.getServerTypeId(), fileInfo.getServerAddress(), fileInfo.getBaseDir(), otherKey);
                if (srcFile != null) {
                    coreCreateRequest.setSrcFile(srcFile);
                }
            }
            String key = createLocalKey(coreCreateRequest);
            updateRequest = updateKey(updateRequest,key,isReadOnly,false);
            BeanUtils.copyCleanProperties(updateRequest,fileInfo);
            updateRequest.setServerTypeId(fileServerConfig.getServerTypeId());
            updateRequest.setServerAddress(fileServerConfig.getServerAddress());
            updateRequest.setBaseDir(fileServerConfig.getBaseDir());
        } else { //如果文件不是本地文件，则创建本地镜像文件
            String remoteKey = (isReadOnly) ? fileInfo.getReadOnlyKey() : fileInfo.getWritableKey();
            if (StringUtils.isNotEmpty(remoteKey)) {
                File srcFile = getSrcFile(fileInfo.getServerTypeId(), fileInfo.getServerAddress(), fileInfo.getBaseDir(), remoteKey);
                if (srcFile != null) {
                    coreCreateRequest.setSrcFile(srcFile);
                }
            }
            String key = createLocalKey(coreCreateRequest);
            updateRequest = updateKey(updateRequest,key,isReadOnly,true);
            BeanUtils.copyCleanProperties(updateRequest,fileInfo);
            updateRequest.setMirrorTypeId(fileServerConfig.getServerTypeId());
            updateRequest.setMirrorAddress(fileServerConfig.getServerAddress());
            updateRequest.setMirrorBaseDir(fileServerConfig.getBaseDir());
        }
        return updateRequest;
    }
    private UpdateNodeFileDTO createUpdateRequestForLocalKey(@NotNull NodeFileDTO nodeFile,String path,boolean isReadOnly) throws CustomException{
        UpdateNodeDTO request = createUpdateRequestForLocalKey(nodeFile,path,0,isReadOnly);
        return BeanUtils.createCleanFrom(request,UpdateNodeFileDTO.class);
    }
    private UpdateNodeFileDTO createUpdateRequestForLocalKey(@NotNull NodeFileDTO nodeFile,boolean isReadOnly) throws CustomException{
        return createUpdateRequestForLocalKey(nodeFile,null,isReadOnly);
    }
    private UpdateNodeDTO createUpdateRequestForLocalKey(@NotNull FullNodeDTO fileNode,long fileLength,boolean isReadOnly) throws CustomException{
        String path = (fileNode.getTextInfo() != null) ? fileNode.getTextInfo().getPath() : null;
        return createUpdateRequestForLocalKey(fileNode.getFileInfo(),path,fileLength,isReadOnly);
    }
    private UpdateNodeDTO createUpdateRequestForLocalKey(@NotNull FullNodeDTO fileNode,boolean isReadOnly) throws CustomException{
        return createUpdateRequestForLocalKey(fileNode,0,isReadOnly);
    }
    private UpdateNodeDTO createUpdateRequestForLocalKey(@NotNull FullNodeDTO fileNode) throws CustomException{
        SimpleNodeDTO nodeInfo = fileNode.getBasic();
        assert(nodeInfo != null);
        return createUpdateRequestForLocalKey(fileNode,nodeInfo.getFileLength(),nodeInfo.getIsReadOnly());
    }

    @Override
    public String getNodePath(SimpleNodeDTO node, Current current) throws CustomException {
        return getNodePathForAccount(getCurrentAccount(current),node,current);
    }

    private String getNodeCompanyIdForAccount(AccountDTO account,@NotNull SimpleNodeDTO node,Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(node.getId());
        query.setNeedCompanyId(LocalConstService.MODE_TRUE);
        query.setOnlyOne(LocalConstService.MODE_TRUE);
        List<FullNodeDTO> fullNodeList = callStorageListFullNode(query,getAccountId(account));
        FullNodeDTO fullNode = ObjectUtils.getFirst(fullNodeList);

        String companyId = null;
        if (fullNode != null) {
            StringElementDTO element = fullNode.getTextInfo();
            if (element != null) {
                companyId = element.getCompanyId();
            }
        }
        return companyId;
    }

    @Override
    public String getNodePathForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        String path = null;
        if (isValid(node)) {
            path = node.getPath();
            if (StringUtils.isEmpty(path)) {
                QueryNodeDTO query = new QueryNodeDTO();
                query.setId(node.getId());
                query.setProjectId(node.getProjectId());
                query.setTaskId(node.getTaskId());
                query.setOnlyOne(LocalConstService.MODE_TRUE);
                List<SimpleNodeDTO> nodeList = listNodeForAccount(account, query, current);
                node = ObjectUtils.getFirst(nodeList);
                CheckService.check(isValid(node), ErrorCode.DataNotFound);
                if (node != null){
                    path = node.getPath();
                }
            }
        }
        return path;
    }

    @Override
    public boolean isEmpty(@NotNull SimpleNodeDTO node, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid(node.getId());
        List<SimpleNodeDTO> list = callStorageListNode(query,null);
        return ObjectUtils.isEmpty(list);
    }

    @Override
    public FullNodeDTO getFullNodeWithHis(@NotNull SimpleNodeDTO node, Current current) throws CustomException {
        return getFullNodeWithHisForAccount(getCurrentAccount(current),node,current);
    }

    @Override
    public FullNodeDTO getFullNodeWithHisForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        QueryNodeInfoDTO infoQuery = new QueryNodeInfoDTO();
        infoQuery.setFileQuery(getFileQuery());
        QueryNodeInfoHistoryDTO hisQuery = new QueryNodeInfoHistoryDTO();
        hisQuery.setHistoryEndTimeStamp(node.getLastModifyTimeStamp());
        infoQuery.setHistoryQuery(hisQuery);
        return getNodeInfoForAccount(account,node,infoQuery,current);
    }

    @Override
    public NodeFileDTO getFileInfo(@NotNull SimpleNodeDTO node, Current current) throws CustomException {
        return getFileInfoForAccount(getCurrentAccount(current),node,current);
    }

    @Override
    @Deprecated
    public NodeFileDTO getFileInfoForAccount(AccountDTO account,  @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        CheckService.check(!node.getIsDirectory(),ErrorCode.InvalidParameter,"getFileInfoForAccount");
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(node.getId());
        List<NodeFileDTO> fileList = callStorageListFile(query,getAccountId(account));
        return ObjectUtils.getFirst(fileList);
    }

    private NodeFileDTO getLocalFileByNode(AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        log.info("\t===>>> getLocalFileByNode启动:" + node.getPath());long t = System.currentTimeMillis();
        CheckService.check(!node.getIsDirectory(),ErrorCode.InvalidParameter);
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFileId(getStorageIdByNode(node));
        query.setServerTypeId(fileServerConfig.getServerTypeId());
        query.setServerAddress(fileServerConfig.getServerAddress());
        query.setBaseDir(fileServerConfig.getBaseDir());
        query.setOnlyOne(LocalConstService.MODE_TRUE);
        List<NodeFileDTO> fileList = callStorageListFile(query,getAccountId(account));
        NodeFileDTO fileNode = ObjectUtils.getFirst(fileList);
        log.info("\t===>>> getLocalFileByNode初步获取:" + node.getPath() + "," + (fileNode != null) + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
        if (fileNode == null){
            NodeFileDTO remoteFile = getFileByNode(account,node,current);
            if (remoteFile != null) {
                CoreFileServer remoteServer = getCoreFileServer(remoteFile.getServerTypeId(), remoteFile.getServerAddress(), remoteFile.getBaseDir());
                CoreFileServer localServer = getCoreFileServer(fileServerConfig.getServerTypeId(), fileServerConfig.getServerAddress(), fileServerConfig.getBaseDir());
                String localReadOnlyKey = null;
                if (StringUtils.isNotEmpty(remoteFile.getReadOnlyKey())) {
                    File mirrorFile = remoteServer.coreGetFile(remoteFile.getReadOnlyKey());
                    localReadOnlyKey = localServer.coreCreateFile(remoteFile.getReadOnlyKey(), mirrorFile);
                }
                String localWritableKey = null;
                if (StringUtils.isNotEmpty(remoteFile.getWritableKey())) {
                    File mirrorFile = remoteServer.coreGetFile(remoteFile.getWritableKey());
                    localWritableKey = localServer.coreCreateFile(remoteFile.getWritableKey(), mirrorFile);
                }
                UpdateNodeFileDTO fileUpdate = BeanUtils.createCleanFrom(remoteFile, UpdateNodeFileDTO.class);
                fileUpdate.setServerTypeId(fileServerConfig.getServerTypeId());
                fileUpdate.setServerAddress(fileServerConfig.getServerAddress());
                fileUpdate.setBaseDir(fileServerConfig.getBaseDir());
                fileUpdate.setReadOnlyKey(localReadOnlyKey);
                fileUpdate.setWritableKey(localWritableKey);
                fileNode = callStorageCreateFile(fileUpdate, getAccountId(account));
            }
        }
        log.info("\t===>>> getLocalFileByNode:" + node.getPath() + "," + (fileNode != null) + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
        return fileNode;
    }

    private List<SimpleNodeDTO> listNodeByFile(AccountDTO account,  @NotNull NodeFileDTO file, String designMode, String caMode, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyId(file.getId());
        query.setDesignMode(designMode);
        query.setCaMode(caMode);
        List<SimpleNodeDTO> nodeList = listNodeForAccount(account,query,current);
        return nodeList;
    }

    private List<SimpleNodeDTO> listNodeByFile(AccountDTO account,  @NotNull NodeFileDTO file, Current current) throws CustomException {
        return listNodeByFile(account,file,null,null,current);
    }

    private List<SimpleNodeDTO> listNodeByFuzzyId(AccountDTO account, @NotNull String fuzzyId, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyId(fuzzyId);
        List<SimpleNodeDTO> nodeList = callStorageListNode(query,getAccountId(account));
        return nodeList;
    }

    private List<FullNodeDTO> listFullNodeByFuzzyId(AccountDTO account, @NotNull String fuzzyId, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyId(fuzzyId);
        List<FullNodeDTO> fullNodeList = callStorageListFullNode(query,getAccountId(account));
        return fullNodeList;
    }

    private NodeFileDTO getFileByNode(AccountDTO account,  @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        CheckService.check(!node.getIsDirectory(),ErrorCode.InvalidParameter);
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFileId(getStorageIdByNode(node));
        query.setIsMirror(LocalConstService.MODE_FALSE);
        query.setOnlyOne(LocalConstService.MODE_TRUE);
        List<NodeFileDTO> fileList = callStorageListFile(query,getAccountId(account));
        return ObjectUtils.getFirst(fileList);
    }

    private QueryNodeInfoFileDTO getFileQuery(){
        QueryNodeInfoFileDTO fileQuery = new QueryNodeInfoFileDTO();
        fileQuery.setMirrorServerTypeId(fileServerConfig.getServerTypeId());
        fileQuery.setMirrorServerAddress(fileServerConfig.getServerAddress());
        fileQuery.setMirrorBaseDir(fileServerConfig.getBaseDir());
        return fileQuery;
    }

    @Override
    public FullNodeDTO getNodeInfo(@NotNull SimpleNodeDTO node, QueryNodeInfoDTO request, Current current) throws CustomException {
        return getNodeInfoForAccount(getCurrentAccount(current),node,request,current);
    }

    @Override
    public FullNodeDTO getNodeInfoForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, QueryNodeInfoDTO request, Current current) throws CustomException {
        QueryNodeDTO query = BeanUtils.createCleanFrom(request,QueryNodeDTO.class);
        query.setId(node.getId());
        query.setNeedPath(LocalConstService.MODE_TRUE);
        query.setNeedProjectName(LocalConstService.MODE_TRUE);
        query.setNeedTaskName(LocalConstService.MODE_TRUE);
        query.setNeedTaskPath(LocalConstService.MODE_TRUE);
        query.setNeedOwnerName(LocalConstService.MODE_TRUE);
        query.setNeedCompanyId(LocalConstService.MODE_TRUE);
        if (request.getFileQuery() != null) {
            query.setNeedFileInfo(LocalConstService.MODE_TRUE);
        }
        query.setOnlyOne(LocalConstService.MODE_TRUE);
        List<FullNodeDTO> fullNodeList = callStorageListFullNode(query,getAccountId(account));
        return ObjectUtils.getFirst(fullNodeList);
    }

    @Override
    public List<SimpleNodeDTO> listChildNode(@NotNull SimpleNodeDTO parent, Current current) throws CustomException {
        return listChildNodeForAccount(getCurrentAccount(current),parent,current);
    }

    @Override
    public List<SimpleNodeDTO> listChildNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO parent, Current current) throws CustomException {
        if (isRootNode(parent)) {
            return listRootNodeForAccount(account,current);
        }
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid(parent.getId());
        query.setAccountId(getAccountId(account));
        return listNodeForAccount(account,query,current);
    }

    @Override
    public List<SimpleNodeDTO> listChildrenNode(SimpleNodeDTO parent, Current current) throws CustomException {
        return listChildrenNodeForAccount(getCurrentAccount(current),parent,current);
    }

    @Override
    public List<SimpleNodeDTO> listChildrenNodeForAccount(AccountDTO account, SimpleNodeDTO parent, Current current) throws CustomException {
        if (isRootNode(parent)) {
            return listAllNodeForAccount(account,current);
        }
        QueryNodeDTO query = new QueryNodeDTO();
        query.setParentPath(parent.getPath());
        return listNodeForAccount(account,query,current);
    }

    private boolean isRootNode(@NotNull SimpleNodeDTO parent){
        return StringUtils.isEmpty(parent.getId());
    }

    @Override
    public List<SimpleNodeDTO> listRootNode(Current current) throws CustomException {
        return listRootNodeForAccount(getCurrentAccount(current),current);
    }

    @Override
    public List<SimpleNodeDTO> listRootNodeForAccount(AccountDTO account, Current current) throws CustomException {
        CheckService.check(StringUtils.isNotEmpty(getAccountId(account)),ErrorCode.NoPermission,"listRootNodeForAccount");
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid("-");
        return listNodeForAccount(account,query,current);
    }

    @Override
    public SimpleNodeDTO getNodeByFuzzyPath(String fuzzyPath, Current current) throws CustomException {
        return getNodeByFuzzyPathForAccount(getCurrentAccount(current),fuzzyPath,current);
    }

    @Override
    public List<SimpleNodeDTO> listWebArchiveDir(String projectId, Current current) throws CustomException {
        return listWebArchiveDirForAccount(getCurrentAccount(current),
                projectId,current);
    }

    @Override
    public List<SimpleNodeDTO> listWebArchiveDirForAccount(AccountDTO account, String projectId, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setAccountId(getAccountId(account));
        query.setProjectId(projectId);
        query.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_DIR_OUTPUT_WEB_ARCHIVE));
        List<SimpleNodeDTO> nodeList = callStorageListSkyNode(query,getAccountId(account));
        return nodeList;
    }


    private boolean isSameServer(CoreFileServer srcServer,CoreFileServer dstServer){
        return (srcServer != null) && (dstServer != null) && (dstServer == srcServer);
    }

    private boolean isWebServer(CoreFileServer server){
        return (server instanceof WebFileServer);
    }

    private boolean isDstWebServer(@NotNull CopyRequestDTO request){
        return (LocalConstService.FILE_SERVER_TYPE_WEB == DigitUtils.parseShort(request.getDstServerTypeId()));
    }

    private boolean isSameServer(@NotNull CopyRequestDTO request){
        boolean isSame = true;
        Short srcTypeId = LocalConstService.FILE_SERVER_TYPE_DISK;
        if ((request.getSrcServerTypeId() != null) && (LocalConstService.FILE_SERVER_TYPE_UNKNOWN == DigitUtils.parseShort(request.getSrcServerTypeId()))) {
            srcTypeId = request.getSrcServerTypeId();
        }
        Short dstTypeId = LocalConstService.FILE_SERVER_TYPE_DISK;
        if ((request.getDstServerTypeId() != null) && (LocalConstService.FILE_SERVER_TYPE_UNKNOWN == DigitUtils.parseShort(request.getDstServerTypeId()))) {
            dstTypeId = request.getDstServerTypeId();
        }

        if (!(srcTypeId.equals(dstTypeId))) {
            isSame = false;
        } else if (!isSameAddress(request.getSrcServerAddress(),request.getDstServerAddress())) {
            isSame = false;
        }

        return isSame;
    }

    private boolean isSameAddress(String srcAddress,String dstAddress){
        return (srcAddress == null) || (dstAddress == null)
                || StringUtils.isSame(StringUtils.getFileServerAddress(srcAddress),StringUtils.getFileServerAddress(dstAddress));
    }

    @Override
    public CommitListResultDTO issueNodeList(@NotNull List<SimpleNodeDTO> srcList, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        return issueNodeListForAccount(getCurrentAccount(current),
                srcList,request,current);
    }

    @Override
    public CommitListResultDTO issueNodeListForAccount(AccountDTO account, @NotNull List<SimpleNodeDTO> srcList, CommitRequestDTO request, Current current) throws CustomException {
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ISSUE));
        return commitNodeListForAccount(account,srcList,request,current);
    }

    @Override
    public SimpleNodeDTO issueNode(@NotNull SimpleNodeDTO src, CommitRequestDTO request, Current current) throws CustomException {
        return issueNodeForAccount(getCurrentAccount(current),
                src,request,current);
    }

    @Override
    public SimpleNodeDTO issueNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, CommitRequestDTO request, Current current) throws CustomException {
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ISSUE));
        return commitNodeForAccount(account,src,request,current);
    }


    @Deprecated
    private void updateStringElement(@NotNull StringElementDTO stringElement, SimpleNodeDTO node, NodeTextDTO txtInfo, CommitRequestDTO request, String actionTypeId){
        BeanUtils.copyCleanProperties(node,stringElement);
        BeanUtils.copyCleanProperties(txtInfo,stringElement);
        stringElement.setPath(txtInfo.getPath());
        BeanUtils.copyCleanProperties(request,stringElement);
        stringElement.setSkyPid(request.getPid());
        stringElement.setActionId(actionTypeId);
        stringElement.setActionName(LocalConstService.getActionName(actionTypeId));
    }

    @Override
    public SimpleNodeDTO changeNodeOwner(SimpleNodeDTO src, UserDTO dstOwner, Current current) throws CustomException {
        return changeNodeOwnerForAccount(getCurrentAccount(current),
                src,dstOwner,current);
    }

    @Override
    public SimpleNodeDTO changeNodeOwnerForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, @NotNull UserDTO dstOwner, Current current) throws CustomException {
        CheckService.check(!isReadOnly(src,account),ErrorCode.NoPermission);
        UpdateNodeDTO updateRequest = new UpdateNodeDTO();
        updateRequest.setOwnerUserId(dstOwner.getId());
        return callStorageUpdateNode(src,null,updateRequest,getAccountId(account));
    }

    private String getAccountId(AccountDTO account){
        return (account != null) ? account.getId() : null;
    }

    private boolean isFile(String nodeType) {
        return !isDirectory(nodeType);
    }

    private boolean isDirectory(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_DIRECTORY);
    }

    private boolean isProject(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_PROJECT);
    }

    //判断节点类型是否分类目录，因目前在节点类型布尔属性内没有定义是否分类目录类型，因此使用是否能被100整除来计算
    private boolean isRange(String nodeType) {
        return (DigitUtils.parseInt(nodeType) % 100) == 0;
    }

    private boolean isIssue(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_ISSUE);
    }

    private boolean isTask(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_ISSUE);
    }

    private boolean isCA(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_CA);
    }

    private boolean isCommit(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_COMMIT);
    }

    private boolean isWeb(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_WEB);
    }

    private boolean isHistory(String nodeType) {
        return LocalConstService.isAttrTrue(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeType, LocalConstService.POS_IS_HISTORY);
    }

    private boolean isReadOnlyType(String nodeType) {
        return isProject(nodeType)
            || isIssue(nodeType)
            || isTask(nodeType)
            || isCA(nodeType)
            || isCommit(nodeType)
            || isWeb(nodeType)
            || isHistory(nodeType);
    }

    private boolean isReadOnly(@NotNull SimpleNodeDTO src,AccountDTO account){
        return isReadOnly(src.getTypeId(),src.getOwnerUserId(),getAccountId(account));
    }

    private boolean isReadOnly(String nodeType,String ownerUserId, String accountId){
        return isReadOnlyType(nodeType)
                || (StringUtils.isNotEmpty(ownerUserId)
                    && (StringUtils.isNotSame(ownerUserId,accountId)));
    }

    private boolean isCanCreateChildType(String nodeType) {
        return isDirectory(nodeType)
                && !isProject(nodeType)
                && !isRange(nodeType)
                && !isCommit(nodeType)
                && !isWeb(nodeType)
                && !isHistory(nodeType);
    }

    private boolean getCanCreateChildType(@NotNull SimpleNodeDTO node, String accountId) {
        return node.getIsTaskRole() && isCanCreateChildType(node.getTypeId());

    }

    private SimpleNodeDTO setAttr(SimpleNodeDTO node, String accountId) {
        if (ObjectUtils.isNotEmpty(node)) {
            String nodeType = node.getTypeId();
            node.setIsReadOnly(isReadOnly(nodeType, node.getOwnerUserId(), accountId));
            node.setCanCreateChild(getCanCreateChildType(node,accountId));
        }
        return node;
    }

    @Override
    public boolean login(LoginDTO loginInfo, Current current) throws CustomException {
        return getUserService().login(loginInfo);
    }

    @Override
    public List<String> setNoticeClient(String userId, NoticeClientPrx client, Current current) throws CustomException {
        getNoticeService().subscribeTopicForUser(userId,client);
        return getNoticeService().listSubscribedTopic(userId);
    }


    private AccountDTO getCurrentAccount(Current current){
        return lastAccount;
//        return getUserService().getCurrent();
    }

    private NoticeServicePrx getNoticeService() throws CustomException{
        return getCommonService().getDefaultNoticeService(null);
    }

    private StorageServicePrx getStoragePrx() throws CustomException {
        return getStorageService(null);
    }

    private UserServicePrx getUserService() throws CustomException {
        return getCommonService().getDefaultUserService(null);
    }

    private CoreFileServer getCoreFileServer(String serverTypeId,String serverAddress,String baseDir) {
        return fileServerConfig.getCoreFileServer(serverTypeId,serverAddress,baseDir);
    }
    private CoreFileServer getCoreFileServer() {
        return getCoreFileServer(null,null,null);
    }

    @Override
    public List<ProjectRoleDTO> listProjectRoleByProjectId(String projectId, Current current) throws CustomException {
        return listProjectRoleByProjectIdForAccount(getCurrentAccount(current),
                projectId,current);
    }

    @Override
    public List<ProjectRoleDTO> listProjectRoleByProjectIdForAccount(AccountDTO account, String projectId, Current current) throws CustomException {
        return getUserService().listProjectRoleByProjectId(projectId);
    }

    @Override
    public List<IdNameDTO> listMajor(Current current) throws CustomException {
        return listMajorForAccount(getCurrentAccount(current),
                current);
    }

    @Override
    public List<IdNameDTO> listMajorForAccount(AccountDTO account, Current current) throws CustomException {
        return LocalConstService.listMajor();
    }

    @Override
    public List<IdNameDTO> listAction(Current current) throws CustomException {
        return listActionForAccount(getCurrentAccount(current),
                current);
    }

    @Override
    public List<IdNameDTO> listActionForAccount(AccountDTO account, Current current) throws CustomException {
        return LocalConstService.listAction();
    }

    @Override
    public boolean deleteNode(SimpleNodeDTO src, Current current) throws CustomException {
        return deleteNodeForAccount(getCurrentAccount(current),
                src,current);
    }

    @Override
    public boolean deleteNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, Current current) throws CustomException {
        if (isValid(src)) {
            CheckService.check((!src.getIsDirectory()) || (isEmpty(src,current)),ErrorCode.DataIsInvalid, "目录非空");
            DeleteAskDTO deleteAsk = new DeleteAskDTO();
            deleteAsk.setLastModifyUserId(getAccountId(account));
            callStorageDeleteNode(src,deleteAsk,getAccountId(account));
        }
        return true;
    }

    @Override
    public boolean setFullNodeLength(FullNodeDTO src, long fileLength, Current current) throws CustomException {
        return false;
    }

    @Override
    public boolean setFullNodeLengthForAccount(AccountDTO account, FullNodeDTO src, long fileLength, Current current) throws CustomException {
        return false;
    }

    @Override
    public boolean setNodeLength(SimpleNodeDTO src, long fileLength, Current current) throws CustomException {
        return setNodeLengthForAccount(getCurrentAccount(current),
                src,fileLength,current);
    }

    @Override
    public void setFileLength(AccountDTO account, @NotNull NodeFileDTO file, long fileLength, Current current) throws CustomException {
        String updatedKey = null;
        String updatedMirrorKey = null;
        String key = file.getWritableKey();
        CoreFileServer coreServer = getCoreFileServer(file.getServerTypeId(), file.getServerAddress(), file.getBaseDir());
        if (StringUtils.isNotEmpty(key)) {
            if (fileLength <= 0) {
                fileLength = coreServer.coreGetFileLength(key);
            }
            coreServer.coreSetFileLength(key, fileLength);
        } else {
            updatedKey = createRealFile(coreServer,fileLength);
        }
        if (!isLocalFile(file)) {
            String mirrorKey = file.getWritableMirrorKey();
            CoreFileServer localServer = getCoreFileServer();
            if (StringUtils.isNotEmpty(mirrorKey)) {
                localServer.coreSetFileLength(mirrorKey, fileLength);
            } else {
                updatedMirrorKey = createRealFile(localServer,fileLength);
            }
        }
        UpdateNodeFileDTO request = new UpdateNodeFileDTO();
        request.setFileLength(Long.toString(fileLength));
        request.setWritableKey(updatedKey);
        callStorageUpdateFile(file,request,getAccountId(account));
    }

    private String getStorageIdByNode(@NotNull SimpleNodeDTO src){
        return StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH);
    }

    @Override
    public boolean setNodeLengthForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, long fileLength, Current current) throws CustomException {
        if (fileLength > 0) {
            long t = System.currentTimeMillis();
            CheckService.check(!src.getIsDirectory(), ErrorCode.InvalidParameter, "setNodeLengthForAccount");
            NodeFileDTO file = getLocalFileByNode(account, src, current);
            log.info("\t===>>> setNodeLengthForAccount1:" + src.getPath() + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
            if ((file != null) && (StringUtils.isNotEmpty(file.getWritableKey()))) {
                String key = file.getWritableKey();
                CoreFileServer localServer = getCoreFileServer();
                localServer.coreSetFileLength(key, fileLength);
                log.info("\t===>>> setNodeLengthForAccount2.1:" + src.getPath() + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
//
//                UpdateNodeFileDTO fileUpdate = new UpdateNodeFileDTO();
//
//                UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
//                nodeUpdate.setFileLength(Long.toString(fileLength));
//                nodeUpdate.setUpdateFileRequest(fileUpdate);
//                callStorageUpdateNodeSimple(src, nodeUpdate, getAccountId(account));
            } else {
                CoreFileServer localServer = getCoreFileServer();
                String key = createRealFile(localServer, src.getPath(), fileLength);
                log.info("\t===>>> setNodeLengthForAccount2.2.1:" + src.getPath() + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();

                UpdateNodeFileDTO fileUpdate = new UpdateNodeFileDTO();
                fileUpdate.setServerTypeId(fileServerConfig.getServerTypeId());
                fileUpdate.setServerAddress(fileServerConfig.getServerAddress());
                fileUpdate.setBaseDir(fileServerConfig.getBaseDir());
                fileUpdate.setWritableKey(key);
                callStorageCreateFile(fileUpdate,getAccountId(account),getStorageIdByNode(src));
//
//                UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
//                nodeUpdate.setFileLength(Long.toString(fileLength));
//                nodeUpdate.setUpdateFileRequest(fileUpdate);
//                callStorageUpdateNodeSimple(src, nodeUpdate, getAccountId(account));
                log.info("\t===>>> setNodeLengthForAccount2.2:" + src.getPath() + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
            }
            log.info("\t===>>> setNodeLengthForAccount:" + src.getPath() + "," + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private NodeFileDTO createFileInBuffer(UpdateNodeFileDTO fileUpdate,SimpleNodeDTO src){
        //因使用了写缓存，需要添加一条记录到读缓存内
        NodeFileDTO file = BeanUtils.createCleanFrom(fileUpdate,NodeFileDTO.class);
        file.setId(StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH));
        List<NodeFileDTO> list = new ArrayList<>();
        list.add(file);
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(src.getId());
        query.setServerTypeId(fileServerConfig.getServerTypeId());
        query.setServerAddress(fileServerConfig.getServerAddress());
        query.setBaseDir(fileServerConfig.getBaseDir());
        String queryKey = JsonUtils.obj2CleanJson(query);
        fileListMap.put(queryKey,list);
        fileIgnoreMap.put(queryKey,System.currentTimeMillis());
        return file;
    }


    @Override
    public boolean releaseNode(SimpleNodeDTO src, long fileLength, Current current) throws CustomException {
        return releaseNodeForAccount(getCurrentAccount(current),
                src,fileLength,current);
    }

    @Override
    public boolean releaseNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, long fileLength, Current current) throws CustomException {
        log.info("\t===>>> 进入 releaseNodeForAccount:" + JsonUtils.obj2CleanJson(src));
        long t0 = System.currentTimeMillis();
        long t = t0;
        CheckService.check(!src.getIsDirectory(),ErrorCode.InvalidParameter,"releaseNodeForAccount");
        NodeFileDTO localFileNode = getLocalFileByNode(account,src,current);
        log.info("\t===>>> releaseNodeForAccount获取本地文件:" + (System.currentTimeMillis()-t) + "ms," + JsonUtils.obj2CleanJson(localFileNode));
        t = System.currentTimeMillis();
        if (localFileNode != null) {
            CheckService.check((localFileNode != null), ErrorCode.DataNotFound);
            String key = getFileKey(localFileNode, false);
            CheckService.check(StringUtils.isNotEmpty(key), ErrorCode.DataIsInvalid);
            CoreFileServer localServer = getCoreFileServer();
            String md5 = localServer.coreCalcMd5(key);
            log.info("\t===>>> releaseNodeForAccount计算md5:" + (System.currentTimeMillis() - t) + "ms," + key);
            t = System.currentTimeMillis();
            if (StringUtils.isNotSame(src.getFileMd5(), md5)) {
                File localFile = localServer.coreGetFile(key);
                if (fileLength <= 0) {
                    fileLength = FileUtils.getFileLength(localFile);
                }
                String path = getNodePathForAccount(account, src, current);
                String localKey = createRealFile(localServer, path, localFile);
                UpdateNodeFileDTO mirrorUpdate = new UpdateNodeFileDTO();
                mirrorUpdate.setReadOnlyKey(localKey);
//                mirrorUpdate.setFileLength(Long.toString(fileLength));
//                mirrorUpdate.setFileMd5(md5);
//                callStorageUpdateFile(localFileNode, mirrorUpdate, getAccountId(account));
                UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
                nodeUpdate.setFileLength(Long.toString(fileLength));
                nodeUpdate.setFileMd5(md5);
                nodeUpdate.setUpdateFileRequest(mirrorUpdate);
                SimpleNodeDTO node = callStorageUpdateNode(src, null, nodeUpdate, getAccountId(account));
                log.info("\t===>>> releaseNodeForAccount更新数据库:" + (System.currentTimeMillis() - t) + "ms," + JsonUtils.obj2CleanJson(node));
                t = System.currentTimeMillis();
//                UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
//                nodeUpdate.setFileLength(mirrorUpdate.getFileLength());
//                nodeUpdate.setFileMd5(mirrorUpdate.getFileMd5());
//                callStorageUpdateNode(src, null, nodeUpdate, getAccountId(account));
//                log.info("\t===>>> releaseNodeForAccount3:" + src.getPath() + "," + (System.currentTimeMillis() - t) + "ms");
//                t = System.currentTimeMillis();
//            NodeFileDTO srcFileNode = getFileByNode(account,src,current);
//            if (!isLocalFile(srcFileNode))
//            {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            CoreFileServer remoteServer = getCoreFileServer(srcFileNode.getServerTypeId(), srcFileNode.getServerAddress(), srcFileNode.getBaseDir());
//                            String remoteReadOnlyKey = createRealFile(remoteServer, path, localFile);
//                            String remoteWritableKey = createRealFile(remoteServer, path, localFile);
//                            UpdateNodeFileDTO remoteUpdate = new UpdateNodeFileDTO();
//                            remoteUpdate.setReadOnlyKey(remoteReadOnlyKey);
//                            remoteUpdate.setWritableKey(remoteWritableKey);
//                            callStorageUpdateFile(localFileNode, remoteUpdate,getAccountId(account));
//                        } catch (CustomException e) {
//                            log.error("发布文件时出错", e);
//                        }
//                    }
//                }.start();
//            }
            }
        }
        log.info("\t===>>> 退出 releaseNodeForAccount:"+ (System.currentTimeMillis()-t0) + "ms," + JsonUtils.obj2CleanJson(src));
        return true;
    }

    @Override
    public boolean reloadNode(SimpleNodeDTO src, Current current) throws CustomException {
        return reloadNodeForAccount(getCurrentAccount(current),
                src,current);
    }

    @Override
    public boolean reloadNodeForAccount(AccountDTO account, SimpleNodeDTO src, Current current) throws CustomException {
        NodeFileDTO file = getFileInfoForAccount(account,src,current);
        if (file != null) {
            String path = getNodePathForAccount(account, src, current);
            reloadFile(account, file, path, current);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SimpleNodeDTO getNodeById(String id, Current current) throws CustomException {
        return getNodeByIdForAccount(getCurrentAccount(current),
                id,current);
    }

    @Override
    public SimpleNodeDTO getNodeByIdForAccount(AccountDTO account, String id, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(id);
        List<SimpleNodeDTO> list = listNodeForAccount(account,query,current);

        SimpleNodeDTO node = null;
        if ((list != null) && (!list.isEmpty())){
            assert (list.size() == 1);
            node = list.get(0);
        }
        return node;
    }

    @Override
    public SimpleNodeDTO getNodeByPath(String path, Current current) throws CustomException {
        return getNodeByPathForAccount(getCurrentAccount(current),
                path,current);
    }

    @Override
    public SimpleNodeDTO getNodeByPathForAccount(AccountDTO account, String path, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPath(path);
        query.setAccountId(getAccountId(account));
        List<SimpleNodeDTO> list = listNodeForAccount(account,query,current);
        return ObjectUtils.getFirst(list);
    }

    @Override
    public List<SimpleNodeDTO> listAllNode(Current current) throws CustomException {
        return listAllNodeForAccount(getCurrentAccount(current),current);
    }

    @Override
    public List<SimpleNodeDTO> listAllNodeForAccount(AccountDTO account, Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        CheckService.check(StringUtils.isNotEmpty(getAccountId(account)),ErrorCode.NoPermission,"listAllNodeForAccount");
        QueryNodeDTO query = new QueryNodeDTO();
        query.setAccountId(getAccountId(account));
        query.setNeedRoleInfo(LocalConstService.MODE_TRUE);
        List<SimpleNodeDTO> list = listNodeForAccount(account,query,current);

        TraceUtils.exit(log,t);
        return list;
    }

    @Override
    public List<SimpleNodeDTO> listNode(QueryNodeDTO query, Current current) throws CustomException {
        return listNodeForAccount(getCurrentAccount(current),
                query,current);
    }

    @Override
    public List<SimpleNodeDTO> listNodeForAccount(AccountDTO account, @NotNull QueryNodeDTO query, Current current) throws CustomException {
        query.setNeedProjectName(LocalConstService.MODE_TRUE);
        query.setNeedTaskName(LocalConstService.MODE_TRUE);
        query.setNeedPath(LocalConstService.MODE_TRUE);
        query.setNeedOwnerName(LocalConstService.MODE_TRUE);
        lastAccount = account;
        return callStorageListNode(query,getAccountId(account));
    }

    @Override
    public List<HistoryDTO> listHistory(@NotNull SimpleNodeDTO node, long startTime, long endTime, Current current) throws CustomException {
        return listHistoryForAccount(getCurrentAccount(current),node,startTime,endTime,current);
    }

    @Override
    public List<HistoryDTO> listHistoryForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, long startTime, long endTime, Current current) throws CustomException {
        if (endTime == 0) {
            endTime = node.getLastModifyTimeStamp();
        }
        QueryNodeInfoHistoryDTO hisQuery = new QueryNodeInfoHistoryDTO();
        hisQuery.setHistoryStartTimeStamp(startTime);
        hisQuery.setHistoryEndTimeStamp(endTime);
        QueryNodeInfoDTO query = new QueryNodeInfoDTO();
        query.setHistoryQuery(hisQuery);
        FullNodeDTO fullNode = getNodeInfoForAccount(account,node,query,current);
        return (fullNode != null) ? fullNode.getHistoryList() : null;
    }

    @Override
    public NodeTextDTO getTextInfo(@NotNull SimpleNodeDTO node, Current current) throws CustomException {
        return getTextInfoForAccount(getCurrentAccount(current),node,current);
    }

    @Override
    public NodeTextDTO getTextInfoForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        QueryNodeInfoTextDTO txtQuery = new QueryNodeInfoTextDTO();
        txtQuery.setIsQueryTypeName(true);
        QueryNodeInfoDTO query = new QueryNodeInfoDTO();
        query.setTextQuery(txtQuery);
        FullNodeDTO fullNode = getNodeInfoForAccount(account,node,query,current);
        return (fullNode != null) ? BeanUtils.createCleanFrom(fullNode.getTextInfo(),NodeTextDTO.class) : null;
    }

    @Override
    public FullNodeDTO getFullNode(SimpleNodeDTO node, Current current) throws CustomException {
        return getFullNodeForAccount(getCurrentAccount(current),
                node,current);
    }

    @Override
    public FullNodeDTO getFullNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO node, Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(node.getId());
        query.setNeedProjectName(LocalConstService.MODE_TRUE);
        query.setNeedTaskName(LocalConstService.MODE_TRUE);
        query.setNeedPath(LocalConstService.MODE_TRUE);
        query.setNeedOwnerName(LocalConstService.MODE_TRUE);
        query.setNeedTaskPath(LocalConstService.MODE_TRUE);
        query.setNeedCompanyId(LocalConstService.MODE_TRUE);
        query.setNeedIssueId(LocalConstService.MODE_TRUE);
        query.setNeedIssuePath(LocalConstService.MODE_TRUE);
        query.setNeedDesignTaskPath(LocalConstService.MODE_TRUE);
        query.setOnlyOne(LocalConstService.MODE_TRUE);
        List<FullNodeDTO> list = callStorageListFullNode(query,getAccountId(account));
        return ObjectUtils.getFirst(list);
    }

    @Override
    public List<NodeFileDTO> listValidFile(AccountDTO account, AskValidFileDTO query, Current current) throws CustomException {
        QueryNodeDTO fileQuery = BeanUtils.createCleanFrom(query,QueryNodeDTO.class);
        fileQuery.setDirectoryMode(LocalConstService.MODE_FALSE);
        return callStorageListFile(fileQuery,getAccountId(account));
    }

    @Override
    public List<NodeFileDTO> listFile(AccountDTO account, @NotNull AskFileDTO query, Current current) throws CustomException {
        QueryNodeDTO fileQuery = BeanUtils.createCleanFrom(query,QueryNodeDTO.class);
        fileQuery.setNeedPath(LocalConstService.MODE_TRUE);
        fileQuery.setNeedProjectName(LocalConstService.MODE_TRUE);
        fileQuery.setNeedTaskName(LocalConstService.MODE_TRUE);
        fileQuery.setNeedOwnerName(LocalConstService.MODE_TRUE);
        fileQuery.setNeedRoleInfo(LocalConstService.MODE_TRUE);
        fileQuery.setAccountId(getAccountId(account));
//        fileQuery.setDirectoryMode(ConstService.MODE_FALSE);
        fileQuery.setNeedFileHistoryInfo(LocalConstService.MODE_TRUE);
        //设置角色掩码
        final int MIN_LENGTH = 3;

        StringBuilder maskBuilder = new StringBuilder();
        for (int i=0; i<MIN_LENGTH;i++){
            maskBuilder.append("0");
        }
        maskBuilder.append(StringUtils.getChar(query.getIsTaskDesigner(), 1, '1'))
            .append(StringUtils.getChar(query.getIsTaskChecker(), 1, '1'))
            .append(StringUtils.getChar(query.getIsTaskAuditor(), 1, '1'));
        fileQuery.setRoleAttr(maskBuilder.toString());

        fileQuery.setIsTaskDesigner(null);
        fileQuery.setIsTaskChecker(null);
        fileQuery.setIsTaskAuditor(null);

        return callStorageListFile(fileQuery,getAccountId(account));
    }

    private boolean isValid(NodeFileDTO file,boolean isReadOnly){
        return (file != null) && (StringUtils.isNotEmpty(file.getId())) && StringUtils.isNotEmpty(getFileKey(file,isReadOnly));
    }

    private boolean isValid(NodeFileDTO file){
        return (file != null) && (StringUtils.isNotEmpty(file.getId()));
    }

    private boolean isValid(SimpleNodeDTO node){
        return (node != null) && (StringUtils.isNotEmpty(node.getId()));
    }

    @Override
    public int writeNode(@NotNull SimpleNodeDTO src, @NotNull FileDataDTO data, Current current) throws CustomException {
        return writeNodeForAccount(getCurrentAccount(current),src,data,current);
    }

    @Override
    public int writeNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, @NotNull FileDataDTO data, Current current) throws CustomException {
        NodeFileDTO file = getLocalFileByNode(account,src,current);
        if (file == null){
            CoreFileServer coreLocalServer = getCoreFileServer();
            String key = coreLocalServer.coreCreateFile(src.getPath());
            String serverTypeId = fileServerConfig.getServerTypeId();
            String serverAddress = fileServerConfig.getServerAddress();
            String serverBaseDir = fileServerConfig.getBaseDir();
            String companyId = getNodeCompanyIdForAccount(account,src,current);
            UpdateNodeFileDTO updateFile = new UpdateNodeFileDTO();
            updateFile.setCompanyId(companyId);
            updateFile.setServerTypeId(serverTypeId);
            updateFile.setServerAddress(serverAddress);
            updateFile.setBaseDir(serverBaseDir);
            updateFile.setWritableKey(key);
            file = callStorageCreateFile(updateFile,getAccountId(account),getStorageIdByNode(src));
//            UpdateNodeDTO updateNode = new UpdateNodeDTO();
//            updateNode.setLastModifyUserId(getAccountId(account));
//            updateNode.setUpdateFileRequest(updateFile);
//            callStorageUpdateNodeSimple(src,updateNode,getAccountId(account));
        }
        String path = getNodePathForAccount(account,src,current);
        boolean rw = isReadOnly(src,account);
        return writeRWFile(account,file,data,path,rw,current);
    }

    @Override
    public FileDataDTO readFile(AccountDTO account, NodeFileDTO file, long pos, int size, Current current) throws CustomException {
        return readFile(account,file,pos,size,true,current);
    }

    public FileDataDTO readFile(AccountDTO account, NodeFileDTO file, long pos, int size, boolean isReadOnly, Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        FileDataDTO data = null;
        String key = getFileKey(file,isReadOnly);
        if (StringUtils.isNotEmpty(key)) {
            CoreFileServer coreFileServer = getCoreFileServer(file.getServerTypeId(),file.getServerAddress(),file.getBaseDir());
            CoreFileDataDTO coreData = coreFileServer.coreReadFile(key,pos,size);
            data = BeanUtils.createFrom(coreData,FileDataDTO.class);
        }

        TraceUtils.exit(log,t,data);
        return data;
    }

    @Override
    public FileDataDTO readNode(@NotNull SimpleNodeDTO src, long pos, int size, Current current) throws CustomException {
        return readNodeForAccount(getCurrentAccount(current),
                src,pos,size,current);
    }

    private boolean isFile(@NotNull SimpleNodeDTO src){
        return !src.getIsDirectory();
    }
    @Override
    public FileDataDTO readNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, long pos, int size, Current current) throws CustomException {
        CheckService.check(isFile(src),ErrorCode.DataIsInvalid);
        NodeFileDTO file = getLocalFileByNode(account,src,current);
        if (file == null) {
            return null;
        }
        boolean rw = isReadOnly(src,account);
        String key = getFileKey(file,rw);
        if (StringUtils.isEmpty(key)) {
            if (rw) {
                releaseNodeForAccount(account, src, 0, current);
            } else {
                reloadNodeForAccount(account, src, current);
            }
            file = getLocalFileByNode(account,src,current);
        }
        return readFile(account,file,pos,size,rw,current);
    }

    @Override
    public SimpleNodeDTO moveNode(@NotNull SimpleNodeDTO src, @NotNull SimpleNodeDTO dstParent, @NotNull MoveNodeRequestDTO request, Current current) throws CustomException {
        return moveNodeForAccount(getCurrentAccount(current),
                src,dstParent,request,current);
    }

    @Override
    public SimpleNodeDTO moveNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, SimpleNodeDTO dstParent, @NotNull MoveNodeRequestDTO request, Current current) throws CustomException {
        UpdateNodeDTO updateRequest = BeanUtils.createCleanFrom(request,UpdateNodeDTO.class);
        updateRequest.setPath(StringUtils.formatPath(request.getFullName()));
        updateRequest.setLastModifyUserId(getAccountId(account));
        log.info("改变节点" + src.getId() + "名称为：" + request.getFullName());
        return callStorageUpdateNode(src,dstParent,updateRequest,getAccountId(account));
    }


    private CommitListResultDTO updateCANodeList(AccountDTO account, List<CANodeDTO> srcList, Current current) throws CustomException {
        UpdateNodeFileDTO fileUpdate = new UpdateNodeFileDTO();
        UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
        nodeUpdate.setLastModifyUserId(getAccountId(account));
        nodeUpdate.setUpdateFileRequest(fileUpdate);

        List<SimpleNodeDTO> successList = new ArrayList<>();
        List<CommitFailDTO> failList = new ArrayList<>();
        for (CANodeDTO src : srcList){
            try {
                fileUpdate.setPassCheck(src.getIsPassCheck() ? LocalConstService.MODE_TRUE : LocalConstService.MODE_FALSE);
                fileUpdate.setPassAudit(src.getIsPassAudit() ? LocalConstService.MODE_TRUE : LocalConstService.MODE_FALSE);
                SimpleNodeDTO dst = callStorageUpdateNodeSimple(getNodeByCANode(account,src, LocalConstService.MODE_FALSE, LocalConstService.MODE_TRUE,current),
                        nodeUpdate,getAccountId(account));
                successList.add(dst);
            } catch (CustomException e) {
                CommitFailDTO failDTO = BeanUtils.createCleanFrom(src,CommitFailDTO.class);
                failList.add(failDTO);
            }
        }
        CommitListResultDTO result = new CommitListResultDTO();
        result.setSuccessList(successList);
        result.setFailList(failList);
        return result;
    }

    @Override
    public CommitListResultDTO updateNodeList(AccountDTO account, List<SimpleNodeDTO> srcList, CommitRequestDTO request, Current current) throws CustomException {
        List<SimpleNodeDTO> successList = new ArrayList<>();
        List<CommitFailDTO> failList = new ArrayList<>();
        for (SimpleNodeDTO src : srcList){
            try {
                SimpleNodeDTO dst = commitNodeForAccount(account,src,request,current);
                successList.add(dst);
            } catch (CustomException e) {
                CommitFailDTO failDTO = BeanUtils.createCleanFrom(src,CommitFailDTO.class);
                failList.add(failDTO);
            }
        }
        CommitListResultDTO result = new CommitListResultDTO();
        result.setSuccessList(successList);
        result.setFailList(failList);
        return result;
    }

    @Override
    public CommitListResultDTO checkNodeListRequest(List<CANodeDTO> srcList, Current current) throws CustomException {
        return checkNodeListRequestForAccount(getCurrentAccount(current),
                srcList,current);
    }

    @Override
    public CommitListResultDTO checkNodeListRequestForAccount(AccountDTO account, List<CANodeDTO> srcList, Current current) throws CustomException {
        return updateCANodeList(account,srcList,current);
    }

    @Override
    public SimpleNodeDTO checkNodeRequest(CANodeDTO src, Current current) throws CustomException {
        return checkNodeRequestForAccount(getCurrentAccount(current),
                src,current);
    }

    @Override
    public SimpleNodeDTO checkNodeRequestForAccount(AccountDTO account, CANodeDTO src, Current current) throws CustomException {
        UpdateNodeFileDTO fileUpdate = new UpdateNodeFileDTO();
        fileUpdate.setPassCheck(src.getIsPassCheck() ? LocalConstService.MODE_TRUE : LocalConstService.MODE_FALSE);
        UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
        nodeUpdate.setLastModifyUserId(getAccountId(account));
        nodeUpdate.setUpdateFileRequest(fileUpdate);
        SimpleNodeDTO srcNode = getNodeByCANode(account,src, LocalConstService.MODE_FALSE, LocalConstService.MODE_TRUE,current);
        return callStorageUpdateNodeSimple(srcNode,nodeUpdate,getAccountId(account));
    }

    @Override
    public CommitListResultDTO auditNodeListRequest(List<CANodeDTO> srcList, Current current) throws CustomException {
        return auditNodeListRequestForAccount(getCurrentAccount(current),
                srcList,current);
    }

    @Override
    public CommitListResultDTO auditNodeListRequestForAccount(AccountDTO account, List<CANodeDTO> srcList, Current current) throws CustomException {
        return updateCANodeList(account,srcList,current);
    }

    @Override
    public SimpleNodeDTO auditNodeRequest(CANodeDTO src, Current current) throws CustomException {
        return auditNodeRequestForAccount(getCurrentAccount(current),
                src,current);
    }

    @Override
    public SimpleNodeDTO auditNodeRequestForAccount(AccountDTO account, CANodeDTO src, Current current) throws CustomException {
        UpdateNodeFileDTO fileUpdate = new UpdateNodeFileDTO();
        fileUpdate.setPassAudit(src.getIsPassAudit() ? LocalConstService.MODE_TRUE : LocalConstService.MODE_FALSE);
        UpdateNodeDTO nodeUpdate = new UpdateNodeDTO();
        nodeUpdate.setLastModifyUserId(getAccountId(account));
        nodeUpdate.setUpdateFileRequest(fileUpdate);
        SimpleNodeDTO srcNode = getNodeByCANode(account,src, LocalConstService.MODE_FALSE, LocalConstService.MODE_TRUE,current);
        return callStorageUpdateNodeSimple(srcNode,nodeUpdate,getAccountId(account));
    }

    private SimpleNodeDTO getNodeByCANode(AccountDTO account,@NotNull CANodeDTO caNode,String designMode, String caMode,Current current) throws CustomException {
        List<SimpleNodeDTO> list = listNodeByFile(account,BeanUtils.createCleanFrom(caNode,NodeFileDTO.class),designMode,caMode,current);
        return ObjectUtils.getFirst(list);
    }

    private List<SimpleNodeDTO> getNodeListByCANodeList(AccountDTO account,@NotNull List<CANodeDTO> caNodeList,String designMode, String caMode,Current current) throws CustomException {
        List<SimpleNodeDTO> nodeList = null;
        for (CANodeDTO caNode : caNodeList){
            List<SimpleNodeDTO> list = listNodeByFile(account,BeanUtils.createCleanFrom(caNode,NodeFileDTO.class),designMode,caMode,current);
            SimpleNodeDTO node = ObjectUtils.getFirst(list);
            if (isValid(node)) {
                if (ObjectUtils.isEmpty(nodeList)) {
                    nodeList = new ArrayList<>();
                }
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    private List<CANodeDTO> getCaNodeListByFileList(AccountDTO account,@NotNull List<NodeFileDTO> fileList,Current current) throws CustomException {
        List<CANodeDTO> caNodeList = new ArrayList<>();
        for (NodeFileDTO file : fileList){
            if (isValid(file)) {
                CANodeDTO caNode = BeanUtils.createCleanFrom(file, CANodeDTO.class);
                caNode.setIsPassCheck(file.getPassCheck());
                caNode.setIsPassAudit(file.getPassAudit());
                caNode.setIsPassDesign(file.getPassDesign());
                caNodeList.add(caNode);
            }
        }
        return caNodeList;
    }

    private List<SimpleNodeDTO> getNodeByCANode(List<CANodeDTO> caNode) throws CustomException {
        return BeanUtils.createListFrom(caNode,SimpleNodeDTO.class);
    }

    @Override
    public CommitListResultDTO requestIssueListForAccount(AccountDTO account, List<CANodeDTO> srcList, SimpleNodeDTO parent, Current current) throws CustomException {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ISSUE));
        request.setPid(parent.getId());
        List<SimpleNodeDTO> nodeList = getNodeListByCANodeList(account,srcList, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        return commitNodeListForAccount(account,nodeList,request,current);
    }

    @Override
    public SimpleNodeDTO requestIssueForAccount(AccountDTO account, CANodeDTO src, SimpleNodeDTO parent, Current current) throws CustomException {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ISSUE));
        request.setPid(parent.getId());
        SimpleNodeDTO srcNode = getNodeByCANode(account,src, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        return commitNodeForAccount(account,srcNode,request,current);
    }

    @Override
    public CommitListResultDTO requestCommitListForAccount(AccountDTO account, @NotNull List<CANodeDTO> srcList, Current current) throws CustomException {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_COMMIT));
        List<SimpleNodeDTO> nodeList = getNodeListByCANodeList(account,srcList, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        return commitNodeListForAccount(account,nodeList,request,current);
    }

    @Override
    public SimpleNodeDTO requestCommitForAccount(AccountDTO account, CANodeDTO src, Current current) throws CustomException {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_COMMIT));
        SimpleNodeDTO srcNode = getNodeByCANode(account,src, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        return commitNodeForAccount(account,srcNode,request,current);
    }

    @Override
    public CommitListResultDTO askCANodeListRequest(List<CANodeDTO> srcList, Current current) throws CustomException {
        return askCANodeListRequestForAccount(getCurrentAccount(current),srcList,current);
    }

    @Override
    public CommitListResultDTO askCANodeListRequestForAccount(AccountDTO account, List<CANodeDTO> srcList, Current current) throws CustomException {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ASK_CA));
        request.setIsPassDesign(true);
        List<SimpleNodeDTO> nodeList = getNodeListByCANodeList(account,srcList, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        return commitNodeListForAccount(account,nodeList,request,current);
    }

    @Override
    public SimpleNodeDTO askCANodeRequest(CANodeDTO src, Current current) throws CustomException {
        return askCANodeRequestForAccount(getCurrentAccount(current),src,current);
    }

    @Override
    public SimpleNodeDTO askCANodeRequestForAccount(AccountDTO account, CANodeDTO src, Current current) throws CustomException {
        log.info("\t===>>> 进入 askCANodeRequestForAccount:" + JsonUtils.obj2CleanJson(src));
        long t0 = System.currentTimeMillis();
        CommitRequestDTO request = new CommitRequestDTO();
        request.setActionTypeId(Short.toString(LocalConstService.STORAGE_ACTION_TYPE_ASK_CA));
        request.setIsPassDesign(true);
        SimpleNodeDTO node = getNodeByCANode(account,src, LocalConstService.MODE_TRUE, LocalConstService.MODE_FALSE,current);
        SimpleNodeDTO dstNode = commitNodeForAccount(account,node,request,current);
        log.info("\t===>>> 退出 askCANodeRequestForAccount:"+ (System.currentTimeMillis()-t0) + "ms," + JsonUtils.obj2CleanJson(dstNode));
        return dstNode;
    }

    @Override
    public CommitListResultDTO commitNodeList(@NotNull List<SimpleNodeDTO> srcList, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        return commitNodeListForAccount(getCurrentAccount(current),
                srcList,request,current);
    }

    @Override
    public CommitListResultDTO commitNodeListForAccount(AccountDTO account, @NotNull List<SimpleNodeDTO> srcList, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        List<SimpleNodeDTO> successList = new ArrayList<>();
        List<CommitFailDTO> failList = new ArrayList<>();
        for (SimpleNodeDTO src : srcList){
            SimpleNodeDTO result = commitNodeForAccount(account,src,request,current);
            if ((result != null) && (!StringUtils.isEmpty(result.getId()))) {
                successList.add(result);
            } else {
                CommitFailDTO failDTO = new CommitFailDTO();
                failDTO.setId(src.getId());
            }
        }
        CommitListResultDTO result = new CommitListResultDTO();
        result.setSuccessList(successList);
        result.setFailList(failList);
        return result;
    }

    @Override
    public SimpleNodeDTO commitNode(@NotNull SimpleNodeDTO src, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        return commitNodeForAccount(getCurrentAccount(current),
                src,request,current);
    }

    private String getActionPath(AccountDTO account, @NotNull SimpleNodeDTO src, @NotNull String actionTypeId, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        //获取目标文件的地址
        String actionName = LocalConstService.getActionName(actionTypeId);

        FullNodeDTO srcFull = getFullNodeForAccount(account,src,current);
        StringElementDTO stringElement = srcFull.getTextInfo();
        //补充参数，包括fileCenter需要的pid和可能用到的actionId,actionName
        stringElement.setSkyPid(request.getPid());
        stringElement.setActionId(actionTypeId);
        stringElement.setActionName(actionName);

        String path = LocalConstService.getActionNodePath(actionTypeId, stringElement);

        //如果未定义，使用当前路径
        if (StringUtils.isEmpty(path)) {
            path = src.getPath();
        }

        //如果是相对路径，添加当前路径
        if (!StringUtils.isAbsolutePath(path)){
            path = StringUtils.appendPath(StringUtils.getDirName(src.getPath()),path);
        }
        //如果与源文件文件名相同，添加动作名称
        if (StringUtils.isSame(path,src.getPath())){
            String fileName = StringUtils.getFileNameWithoutExt(path) + StringUtils.SPLIT_NAME_PART +
                    actionName + StringUtils.getFileExt(path);
            path = StringUtils.appendPath(StringUtils.getDirName(path),fileName);
        }
        return StringUtils.formatPath(path);
    }

    @Override
    public SimpleNodeDTO commitNodeForAccount(AccountDTO account, @NotNull SimpleNodeDTO src, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        //确定和调整文件提交动作
        String actionTypeId = request.getActionTypeId();
        if (StringUtils.isEmpty(actionTypeId)){
            actionTypeId = Short.toString(LocalConstService.STORAGE_ACTION_TYPE_COMMIT);
        }

        request.setActionTypeId(actionTypeId);

        String path = getActionPath(account,src,actionTypeId,request,current);
        SimpleNodeDTO targetNode = getNodeByPathForAccount(account,path,current);
        if (!isValid(targetNode)){
            targetNode = createVersion(account,src,path,request,current);
        } else {
            targetNode = updateVersion(account,src,targetNode,request,current);
        }

        //更新源节点状态
        if (request.getIsPassDesign() || request.getIsPassCheck() || request.getIsPassAudit()) {
            UpdateNodeFileDTO srcFileRequest = new UpdateNodeFileDTO();
            if (request.getIsPassDesign()) {
                srcFileRequest.setPassDesign(LocalConstService.MODE_TRUE);
            }
            if (request.getIsPassCheck()) {
                srcFileRequest.setPassCheck(LocalConstService.MODE_TRUE);
            }
            if (request.getIsPassAudit()) {
                srcFileRequest.setPassAudit(LocalConstService.MODE_TRUE);
            }
            UpdateNodeDTO srcNodeRequest = new UpdateNodeDTO();
            srcNodeRequest.setLastModifyUserId(getAccountId(account));
            srcNodeRequest.setUpdateFileRequest(srcFileRequest);
            callStorageUpdateNodeSimple(src,srcNodeRequest,getAccountId(account));
        }

        return targetNode;
    }

    @Override
    public SimpleNodeDTO getNodeByFuzzyPathForAccount(AccountDTO account,String fuzzyPath,Current current) throws CustomException {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFuzzyPath(fuzzyPath);
        query.setAccountId(getAccountId(account));
        List<SimpleNodeDTO> list = callStorageListNode(query,getAccountId(account));
        return ((list != null) && (!list.isEmpty())) ? list.get(0) : null;
    }

    private boolean isCoreFileServer(Short serverTypeId, String serverAddress){
        boolean isCore = LocalConstService.FILE_SERVER_TYPE_DISK != DigitUtils.parseShort(serverTypeId);
        if (!isCore) isCore = StringUtils.isSame(serverAddress, fileServerConfig.getServerAddress());
        return isCore;
    }

    private String getUniquePath(AccountDTO account, @NotNull String path, Current current) throws CustomException {
        String dir = StringUtils.getDirName(path);
        String fn = StringUtils.getFileNameWithoutExt(path);
        String ext = StringUtils.getFileExt(path);
        assert (fn != null);
        fn = StringUtils.addTimeStamp(fn);
        StringBuilder keyBuilder = new StringBuilder(dir);
        if (keyBuilder.length() > 0) {
            keyBuilder.append(StringUtils.SPLIT_PATH);
        }
        keyBuilder.append(fn);
        final int MAX_UNIQUE_NUM = 1000;
        int i = 0;
        String tmpKey;
        do {
            tmpKey = keyBuilder.toString();
            if (i > 0) {
                tmpKey += StringUtils.SPLIT_NAME_PART + i;
            }
            if (StringUtils.isNotEmpty(ext)) {
                tmpKey += ext;
            }
        } while (i++<MAX_UNIQUE_NUM && (getNodeByPathForAccount(account,tmpKey,current) != null));
        path = tmpKey;

        return path;
    }
    
    @Override
    public SimpleNodeDTO createVersion(AccountDTO account, @NotNull SimpleNodeDTO src, String path, CommitRequestDTO request, Current current) throws CustomException {
        //发布改写内容
        releaseNodeForAccount(account,src,0,current);

        //获取父节点
        path = StringUtils.formatPath(path);
        SimpleNodeDTO parent = null;
        if (!StringUtils.isAbsolutePath(path)){
            parent = getNodeByIdForAccount(account,src.getPid(),current); 
        }

        //获取目标服务器
        String actionTypeId = request.getActionTypeId();
        String serverTypeId = request.getServerTypeId();
        if (StringUtils.isEmpty(serverTypeId)){
            serverTypeId = LocalConstService.getActionFileServerTypeId(actionTypeId);
        }
        if (StringUtils.isEmpty(serverTypeId)){
            serverTypeId = fileServerConfig.getServerTypeId();
        }
        String serverAddress = request.getServerAddress();
        if (StringUtils.isEmpty(serverAddress)){
            serverAddress = LocalConstService.getActionFileServerAddress(actionTypeId);
            serverAddress = fileServerConfig.getServerAddress(serverTypeId,serverAddress);
        }
        String baseDir = request.getBaseDir();
        if (StringUtils.isEmpty(baseDir)){
            baseDir = LocalConstService.getActionFileServerBaseDir(actionTypeId);
            baseDir = fileServerConfig.getBaseDir(serverTypeId,baseDir);
        }

        //复制文件
        NodeFileDTO srcFile = getLocalFileByNode(account,src,current);
        String srcKey = getFileKey(srcFile,true);
        if (StringUtils.isEmpty(srcKey)) {
            srcKey = getFileKey(srcFile,false);
        }
        CheckService.check(StringUtils.isNotEmpty(srcKey), ErrorCode.DataIsInvalid,"没有实际内容");
        File localFile = getCoreFileServer().coreGetFile(srcKey);
        String dstPath = (parent != null) ? StringUtils.appendPath(getNodePathForAccount(account,parent,current),path) : path;
        SimpleNodeDTO node = getNodeByPathForAccount(account,dstPath,current);
        if (node != null) {
            dstPath = getUniquePath(account,dstPath,current);
            path = StringUtils.getDirName(path);
            if (StringUtils.isNotEmpty(path)) {
                path += StringUtils.SPLIT_PATH;
            }
            path += StringUtils.getFileName(dstPath);
        }
        CoreFileServer fileServer = getCoreFileServer(serverTypeId,serverAddress,baseDir);
        String dstKey = createRealFile(fileServer,dstPath,localFile);

        //获取目标文件的类型
        String nodeTypeId = LocalConstService.getActionNodeTypeId(actionTypeId);

        //创建记录
        UpdateHistoryDTO hisRequest = BeanUtils.createCleanFrom(request,UpdateHistoryDTO.class);

        UpdateNodeFileDTO fileRequest = BeanUtils.createCleanFrom(request,UpdateNodeFileDTO.class);
        fileRequest.setCompanyId(getNodeCompanyIdForAccount(account,src,current));
        fileRequest.setServerTypeId(serverTypeId);
        fileRequest.setServerAddress(serverAddress);
        fileRequest.setBaseDir(baseDir);
        fileRequest.setReadOnlyKey(dstKey);
        fileRequest.setMainFileId(StringUtils.left(src.getId(),StringUtils.DEFAULT_ID_LENGTH));
        fileRequest.setHistoryUpdateRequest(hisRequest);

        UpdateNodeDTO nodeRequest = BeanUtils.createCleanFrom(src,UpdateNodeDTO.class);
        BeanUtils.copyCleanProperties(request,nodeRequest);
        nodeRequest.setTypeId(nodeTypeId);
        nodeRequest.setPath(path);
        nodeRequest.setPid(null);
        nodeRequest.setLastModifyUserId(getAccountId(account));
        nodeRequest.setUpdateFileRequest(fileRequest);

        SimpleNodeDTO dstNode = callStorageCreateNode(parent,nodeRequest,getAccountId(account));
        dstNode.setProjectName(src.getProjectName());
        dstNode.setTaskName(src.getTaskName());
        dstNode.setOwnerName(src.getOwnerName());

        //发送通知消息
//        String typeIdString = ConstService.getActionNoticeTypeIdString(request.getActionTypeId());
//        if (StringUtils.isNotEmpty(typeIdString)){
//            FullNodeDTO srcFull = getFullNodeForAccount(account,src,current);
//            StringElementDTO stringElement = srcFull.getTextInfo();
//
//            NoticeRequestDTO noticeRequest = new NoticeRequestDTO();
//            noticeRequest.setTypeIdString(typeIdString);
//            noticeRequest.setStringElement(stringElement);
//            getNoticeService().sendNoticeForAccountAsync(account,noticeRequest);
//        }

        return dstNode;
    }

    private SimpleNodeDTO callStorageCreateNode(SimpleNodeDTO parent,UpdateNodeDTO request,String accountId) throws CustomException {
        SimpleNodeDTO node = getStoragePrx().createNode(parent,request);
        clearNodeFromMap(parent,nodeListMap,nodeIgnoreMap);
        return setAttr(node,accountId);
    }

    private SimpleNodeDTO callStorageUpdateNode(SimpleNodeDTO src,SimpleNodeDTO parent,UpdateNodeDTO request,String accountId) throws CustomException {
        SimpleNodeDTO node = getStoragePrx().updateNode(src,parent,request);
        node = setAttr(node,accountId);
        updateNodeListMap(node,nodeListMap,nodeIgnoreMap);
        if (request != null && request.getUpdateFileRequest() != null){
            NodeFileDTO file = getLocalFileByNode(null,node,null);
            updateFileListMap(file,fileListMap,fileIgnoreMap);
        }
        return node;
    }

    private void flushUpdateNodeBuffer(Current current) throws CustomException {
        Map<String,UpdateNodeDTO> lastNodeUpdateMap = nodeUpdateMap;
        Map<String,SimpleNodeDTO> lastNodeUpdateParentMap = nodeUpdateParentMap;
        nodeUpdateMap = new HashMap<>();
        nodeUpdateParentMap = new HashMap<>();
        for (Map.Entry<String,UpdateNodeDTO> entry : lastNodeUpdateMap.entrySet()){
            SimpleNodeDTO src = JsonUtils.json2Obj(entry.getKey(),SimpleNodeDTO.class);
            SimpleNodeDTO parent = lastNodeUpdateParentMap.get(entry.getKey());
            getStoragePrx().updateNode(src,parent,entry.getValue());
//            clearNodeFromMap(src,nodeListMap,nodeIgnoreMap);
//            clearNodeFromMap(parent,nodeListMap,nodeIgnoreMap);
        }
    }

    private SimpleNodeDTO callStorageUpdateNodeSimple(SimpleNodeDTO src,UpdateNodeDTO request,String accountId) throws CustomException {
        return callStorageUpdateNode(src,null,request,accountId);
    }


    private void callStorageDeleteNode(SimpleNodeDTO src,DeleteAskDTO request,String accountId) throws CustomException {
        getStoragePrx().deleteNode(src,request);
        clearNodeFromMap(src, nodeListMap, nodeIgnoreMap);
    }

    private List<SimpleNodeDTO> callStorageListNode(QueryNodeDTO query,String accountId) throws CustomException {
        query.setPath(StringUtils.formatPath(query.getPath()));
        query.setParentPath(StringUtils.formatPath(query.getParentPath()));
        query.setFuzzyPath(StringUtils.formatPath(query.getFuzzyPath()));

        String key = JsonUtils.obj2CleanJson(query);
        log.info("FileService.listNode：" + key);

        List<SimpleNodeDTO> list = nodeListMap.get(key);
        Long lastTime = nodeIgnoreMap.get(key);
        long currentTime = System.currentTimeMillis();
        if ((lastTime == null) || ((currentTime - lastTime) > NODE_IGNORE_TIME)) {
            list = getStoragePrx().listNode(query);
            if (ObjectUtils.isNotEmpty(list)) {
                for (SimpleNodeDTO node : list) {
                    setAttr(node, accountId);
                }
                nodeListMap.put(key, list);
                nodeIgnoreMap.put(key,currentTime);
                removeBufferEarliest(nodeListMap,nodeIgnoreMap,MAX_BUFFER_SIZE);
            }
        } else {
            nodeIgnoreMap.put(key,currentTime);
        }

        return list;
    }

    private List<SimpleNodeDTO> callStorageListSkyNode(QueryNodeDTO query,String accountId) throws CustomException {
        query.setPath(StringUtils.formatPath(query.getPath()));
        query.setParentPath(StringUtils.formatPath(query.getParentPath()));
        query.setFuzzyPath(StringUtils.formatPath(query.getFuzzyPath()));

        String key = JsonUtils.obj2CleanJson(query);
        log.info("FileService.listSkyNode：" + key);

        List<SimpleNodeDTO> list = skyNodeListMap.get(key);
        Long lastTime = skyNodeIgnoreMap.get(key);
        long currentTime = System.currentTimeMillis();
        if ((lastTime == null) || ((currentTime - lastTime) > NODE_IGNORE_TIME)) {
            list = getStoragePrx().listOldNode(query);
            if (ObjectUtils.isNotEmpty(list)) {
                for (SimpleNodeDTO node : list) {
                    setAttr(node, accountId);
                }
                skyNodeListMap.put(key, list);
                skyNodeIgnoreMap.put(key,currentTime);
                removeBufferEarliest(skyNodeListMap,skyNodeIgnoreMap,MAX_BUFFER_SIZE);
            }
        } else {
            skyNodeIgnoreMap.put(key,currentTime);
        }

        return list;
    }

    private List<NodeFileDTO> callStorageListFile(QueryNodeDTO query,String accountId) throws CustomException {
        query.setPath(StringUtils.formatPath(query.getPath()));
        query.setParentPath(StringUtils.formatPath(query.getParentPath()));
        query.setFuzzyPath(StringUtils.formatPath(query.getFuzzyPath()));

        String key = JsonUtils.obj2CleanJson(query);
        log.info("FileService.listFile：" + key);

        List<NodeFileDTO> list = fileListMap.get(key);
        Long aliveTime = fileIgnoreMap.get(key);
        long currentTime = System.currentTimeMillis();
        if ((list == null) || (aliveTime == null) || (currentTime < aliveTime)) {
            list = getStoragePrx().listFile(query);
            setFileList(key,list,(long)FILE_IGNORE_TIME);
        } else {
            fileIgnoreMap.put(key,currentTime + FILE_IGNORE_TIME);
        }

        return list;
    }

    private <T> void removeBufferEarliest(Map<String,List<T>> listBuffer, Map<String,Long> listLastTime, int maxBufferSize){
        if (ObjectUtils.isEmpty(listBuffer) || ObjectUtils.isEmpty(listLastTime) || (maxBufferSize <= 0)){
            return;
        }
        if (listBuffer.size() > maxBufferSize){
            String key = null;
            Long lastTime = null;
            for (Map.Entry<String,Long> entry : listLastTime.entrySet()){
                if ((lastTime == null) || (lastTime > entry.getValue())){
                    lastTime = entry.getValue();
                    key = entry.getKey();
                }
            }
            if (StringUtils.isNotEmpty(key)){
                listBuffer.remove(key);
                listLastTime.remove(key);
            }
        }

    }

    private List<FullNodeDTO> callStorageListFullNode(QueryNodeDTO query,String accountId) throws CustomException {
        query.setPath(StringUtils.formatPath(query.getPath()));
        query.setParentPath(StringUtils.formatPath(query.getParentPath()));
        query.setFuzzyPath(StringUtils.formatPath(query.getFuzzyPath()));

        String key = JsonUtils.obj2CleanJson(query);
        log.info("FileService.listFile：" + key);

        List<FullNodeDTO> list = fullNodeListMap.get(key);
        Long lastTime = fullNodeIgnoreMap.get(key);
        long currentTime = System.currentTimeMillis();
        if ((lastTime == null) || ((currentTime - lastTime) > NODE_IGNORE_TIME)) {
            list = getStoragePrx().listFullNode(query);
            if (ObjectUtils.isNotEmpty(list)) {
                fullNodeListMap.put(key, list);
                fullNodeIgnoreMap.put(key,currentTime);
                removeBufferEarliest(fullNodeListMap,fullNodeIgnoreMap,MAX_BUFFER_SIZE);
            }
        } else {
            fullNodeIgnoreMap.put(key,currentTime);
        }

        return list;
    }

    @Override
    public void flushBuffer(Current current) throws CustomException {
        flushUpdateFileBuffer(current);
        flushUpdateNodeBuffer(current);
    }

    private NodeFileDTO callStorageUpdateFile(NodeFileDTO src, UpdateNodeFileDTO request, String accountId) throws CustomException {
        NodeFileDTO file = getStoragePrx().updateFile(src,request);
        updateFileListMap(file,fileListMap,fileIgnoreMap);
        return file;
    }

    private void updateNodeListMap(SimpleNodeDTO node,Map<String,List<SimpleNodeDTO>> nListMap,Map<String,Long> ignoreMap) throws CustomException{
        if (!isValid(node)) {
            return;
        }
        for (Map.Entry<String,List<SimpleNodeDTO>> entry : nListMap.entrySet()){
            for (SimpleNodeDTO n : entry.getValue()){
                if (StringUtils.isSame(node.getId(),n.getId())){
                    entry.getValue().remove(n);
                    entry.getValue().add(node);
                    ignoreMap.put(entry.getKey(),System.currentTimeMillis());
                }
            }
        }

    }

    private void updateFileListMap(NodeFileDTO file, @NotNull Map<String,List<NodeFileDTO>> fListMap, @NotNull Map<String,Long> ignoreMap) throws CustomException{
        if (!isValid(file)) {
            return;
        }

        QueryNodeDTO prepareQuery = new QueryNodeDTO();
        prepareQuery.setFileId(file.getId());
        prepareQuery.setServerTypeId(fileServerConfig.getServerTypeId());
        prepareQuery.setServerAddress(fileServerConfig.getServerAddress());
        prepareQuery.setBaseDir(fileServerConfig.getBaseDir());
        prepareQuery.setOnlyOne(LocalConstService.MODE_TRUE);
        String key = JsonUtils.obj2CleanJson(prepareQuery);
        boolean found = false;
        for (Map.Entry<String,List<NodeFileDTO>> entry : fListMap.entrySet()){
            for (NodeFileDTO f : entry.getValue()){
                if (StringUtils.isSame(file.getId(),f.getId())){
                    entry.getValue().remove(f);
                    entry.getValue().add(file);
                    ignoreMap.put(entry.getKey(),System.currentTimeMillis());
                }
                if (StringUtils.isSame(key,entry.getKey())){
                    found = true;
                }
            }
        }
        if (!found) {
            List<NodeFileDTO> list = new ArrayList<>();
            list.add(file);
            setFileList(key,list,(long)FILE_IGNORE_TIME);
        }
    }

    private void setFileList(String key, List<NodeFileDTO> list, Long aliveTime){
        fileListMap.put(key,list);
        if (aliveTime != null) {
            fileIgnoreMap.put(key, System.currentTimeMillis() + aliveTime);
        }
        removeBufferEarliest(fileListMap,fileIgnoreMap,MAX_BUFFER_SIZE);
    }

    private void flushUpdateFileBuffer(Current current) throws CustomException {
        fileUpdateMap = new HashMap<>();
        Map<String,UpdateNodeFileDTO> lastFileUpdateMap = fileUpdateMap;
        for (Map.Entry<String,UpdateNodeFileDTO> entry : lastFileUpdateMap.entrySet()){
            NodeFileDTO file = JsonUtils.json2Obj(entry.getKey(),NodeFileDTO.class);
            getStoragePrx().updateFile(file,entry.getValue());
//            clearFileFromMap(file,fileListMap,fileIgnoreMap);
        }
    }

    private NodeFileDTO callStorageCreateFile(UpdateNodeFileDTO request, String accountId) throws CustomException {
        return callStorageCreateFile(request,accountId,null);
    }

    private NodeFileDTO callStorageCreateFile(UpdateNodeFileDTO request, String accountId, String id) throws CustomException {
        NodeFileDTO file = getStoragePrx().createFileWithId(request,id);
        updateFileListMap(file,fileListMap,fileIgnoreMap);
        return file;
    }

    private void clearNodeFromMap(@NotNull String id,Map<String,List<SimpleNodeDTO>> nListMap,Map<String,Long> ignoreMap){
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String,List<SimpleNodeDTO>> entry : nListMap.entrySet()){
            for (SimpleNodeDTO n : entry.getValue()){
                if (StringUtils.isSame(id,n.getId())){
                    keyList.add(entry.getKey());
                    break;
                }
            }
        }
        for (String key : keyList){
            nListMap.remove(key);
            ignoreMap.remove(key);
        }
        clearFullNodeFromMap(id,fullNodeListMap,fullNodeIgnoreMap);
    }

    private void clearNodeFromMap(SimpleNodeDTO node,Map<String,List<SimpleNodeDTO>> nListMap,Map<String,Long> ignoreMap){
        if (!isValid(node)) {
            return;
        }
        clearNodeFromMap(node.getId(),nListMap,ignoreMap);
    }

    private void clearFileFromMap(NodeFileDTO file,Map<String,List<NodeFileDTO>> fListMap,Map<String,Long> ignoreMap) throws CustomException {
        if (!isValid(file)) {
            return;
        }
        clearFileFromMap(file.getId(),fListMap,ignoreMap);
    }

    private void clearFileFromMap(String fileId,Map<String,List<NodeFileDTO>> fListMap,Map<String,Long> ignoreMap) throws CustomException {
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String,List<NodeFileDTO>> entry : fListMap.entrySet()){
            for (NodeFileDTO f : entry.getValue()){
                if (StringUtils.isSame(fileId,f.getId())){
                    keyList.add(entry.getKey());
                    break;
                }
            }
        }
        if (ObjectUtils.isEmpty(keyList)) {
            for (String key : keyList) {
                fListMap.remove(key);
                ignoreMap.remove(key);
            }
        }
    }

    private void clearFullNodeFromMap(SimpleNodeDTO node,Map<String,List<FullNodeDTO>> nListMap,Map<String,Long> ignoreMap){
        if (node == null) {
            return;
        }
        clearFullNodeFromMap(node.getId(),nListMap,ignoreMap);
    }

    private void clearFullNodeFromMap(@NotNull String id,Map<String,List<FullNodeDTO>> nListMap,Map<String,Long> ignoreMap){
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String,List<FullNodeDTO>> entry : nListMap.entrySet()){
            for (FullNodeDTO n : entry.getValue()){
                if ((isValid(n.getBasic())) && StringUtils.isSame(id,n.getBasic().getId())){
                    keyList.add(entry.getKey());
                    break;
                }
            }
        }
        for (String key : keyList){
            nListMap.remove(key);
            ignoreMap.remove(key);
        }
    }

    private String getBackupNodeTypeId(String actionTypeId) {
        return LocalConstService.getExtra(LocalConstService.CLASSIC_TYPE_ACTION,actionTypeId,8);
    }

    private String getNodeTypeName(String nodeTypeId) {
        return LocalConstService.getTitle(LocalConstService.CLASSIC_TYPE_STORAGE_NODE,nodeTypeId);
    }

    @Override
    public SimpleNodeDTO updateVersion(AccountDTO account, @NotNull SimpleNodeDTO src, @NotNull SimpleNodeDTO dst, @NotNull CommitRequestDTO request, Current current) throws CustomException {
        //备份原有节点
        String dstPath = getNodePathForAccount(account,dst,current);
        String backupDirTypeId = getBackupNodeTypeId(request.getActionTypeId());
        String backupDirName = getNodeTypeName(backupDirTypeId);

        MoveNodeRequestDTO moveRequest = new MoveNodeRequestDTO();
        String backupDir = StringUtils.getDirName(dst.getPath());
        backupDir += StringUtils.SPLIT_PATH + backupDirName;
        SimpleNodeDTO parent = getNodeByPathForAccount(account,backupDir,current);
        if ((parent == null) && (StringUtils.isNotEmpty(StringUtils.getDirName(dst.getPath())))){
            parent = getNodeByPathForAccount(account,StringUtils.getDirName(dst.getPath()),current);
            if (parent != null) {
                UpdateNodeDTO createBackupRequest = new UpdateNodeDTO();
                createBackupRequest.setPath(backupDirName);
                createBackupRequest.setTypeId(backupDirTypeId);
                createBackupRequest.setOwnerUserId(getAccountId(account));
                createBackupRequest.setLastModifyUserId(getAccountId(account));
                parent = callStorageCreateNode(parent,createBackupRequest,getAccountId(account));
            }
        }
        String backupFileName = StringUtils.getFileNameWithoutExt(dst.getName());
        backupFileName += StringUtils.SPLIT_NAME_PART + StringUtils.getTimeStamp();
        backupFileName += StringUtils.getFileExt(dst.getName());
        moveRequest.setFullName(backupFileName);
        moveNodeForAccount(account, dst, parent, moveRequest, current);

        //发布源节点
        return createVersion(account,src,dstPath,request,current);
    }

    @Override
    public SimpleNodeDTO createDirectory(SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        return createDirectoryForAccount(getCurrentAccount(current),parent,request,current);
    }

    @Override
    public SimpleNodeDTO createDirectoryForAccount(AccountDTO account, SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        request.setIsDirectory(true);
        return createNodeForAccount(account,parent,request,current);
    }

    @Override
    public SimpleNodeDTO createFile(SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        return createFileForAccount(getCurrentAccount(current),parent,request,current);
    }

    @Override
    public SimpleNodeDTO createFileForAccount(AccountDTO account, SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        request.setIsDirectory(false);
        return createNodeForAccount(account,parent,request,current);
    }

    @Override
    public SimpleNodeDTO createNode(SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        return createNodeForAccount(getCurrentAccount(current),parent,request,current);
    }

    @Override
    public SimpleNodeDTO createNodeForAccount(AccountDTO account, SimpleNodeDTO parent, @NotNull CreateNodeRequestDTO request, Current current) throws CustomException {
        log.info("\t===>>> 进入 createNodeForAccount:" + JsonUtils.obj2CleanJson(parent) + JsonUtils.obj2CleanJson(request));
        long t0 = System.currentTimeMillis();
        //填充与输入相关属性
        UpdateNodeDTO createRequest = BeanUtils.createCleanFrom(request,UpdateNodeDTO.class);
        String accountId = getAccountId(account);
        createRequest.setLastModifyUserId(accountId);
        createRequest.setOwnerUserId(accountId);
        createRequest.setPath(request.getFullName());
        //填充与父节点相关属性
        if (!isValid(parent)) {
            createRequest.setProjectId(parent.getProjectId());
            createRequest.setTaskId(parent.getTaskId());
        } else {
            createRequest.setProjectId(null);
            createRequest.setTaskId(null);
        }
        //填充文件类型
        if (request.getIsDirectory()) {
            if (isValid(parent)) {
                createRequest.setTypeId(LocalConstService.getPathType(parent.getTypeId()));
            } else {
                createRequest.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_DIR_UNKNOWN));
            }
        } else {
            if (request.getFileLength() > 0) {
                if (isValid(parent)) {
                    createRequest.setTypeId(LocalConstService.getFileType(parent.getTypeId()));
                } else {
                    createRequest.setTypeId(Short.toString(LocalConstService.STORAGE_NODE_TYPE_UNKNOWN));
                }
                createRequest.setFileLength(Long.toString(request.getFileLength()));
                //创建实体文件
                String path = getNodePathForAccount(account,parent,current);
                path = StringUtils.appendPath(path,createRequest.getPath());
                long fileLength = DigitUtils.parseLong(createRequest.getFileLength());
                String key = createRealFile(getCoreFileServer(),path,fileLength);
                //创建文件生成申请
                UpdateNodeFileDTO createFileRequest = new UpdateNodeFileDTO();
                createFileRequest.setCompanyId(getNodeCompanyIdForAccount(account,parent,current));
                createFileRequest.setServerTypeId(fileServerConfig.getServerTypeId());
                createFileRequest.setServerAddress(fileServerConfig.getServerAddress());
                createFileRequest.setBaseDir(fileServerConfig.getBaseDir());
                createFileRequest.setWritableKey(key);
                createRequest.setUpdateFileRequest(createFileRequest);
            } else {
                createRequest.setFileLength(null);
            }
        }
        SimpleNodeDTO node =  callStorageCreateNode(parent,createRequest,getAccountId(account));
        node.setIsReadOnly(false);
        node.setCanCreateChild(node.getIsDirectory());
        log.info("\t===>>> 退出 createNodeForAccount:" + (System.currentTimeMillis()-t0) + "ms," + JsonUtils.obj2CleanJson(node));
        return node;
    }

    private String createRealFile(@NotNull CoreFileServer coreFileServer, @NotNull CoreCreateFileRequest request) throws CustomException {
        try {
            return coreFileServer.coreCreateFile(BeanUtils.cleanProperties(request));
        } catch (WebException e) {
            throw new CustomException(ErrorCode.Assert,e.getMessage());
        }
    }

    private String createRealFile(@NotNull CoreFileServer coreFileServer, String path, @NotNull File localFile) throws CustomException {
        CoreCreateFileRequest request = new CoreCreateFileRequest();
        request.setPath(path);
        request.setSrcFile(localFile);
        return createRealFile(coreFileServer,request);
    }

    private String createRealFile(@NotNull CoreFileServer coreFileServer, String path, long fileLength) throws CustomException {
        CoreCreateFileRequest request = new CoreCreateFileRequest();
        request.setPath(path);
        request.setFileLength(fileLength);
        return createRealFile(coreFileServer,request);
    }

    private String createRealFile(@NotNull CoreFileServer coreFileServer, long fileLength) throws CustomException {
        return createRealFile(coreFileServer,null,fileLength);
    }

}
