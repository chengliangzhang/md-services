package com.maoding.FileServer;


import com.maoding.common.config.IceConfig;
import com.maoding.common.servicePrx.FileServicePrxImpl;
import com.maoding.common.servicePrx.StorageServicePrxImpl;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.ObjectUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.fileServer.zeroc.*;
import com.maoding.storage.zeroc.*;
import com.maoding.user.zeroc.AccountDTO;
import com.maoding.user.zeroc.LoginDTO;
import com.maoding.user.zeroc.WebRoleDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* FileServiceImpl Tester.
*
* @author Zhangchengliang
* @since 01/11/2018
* @version 1.0
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})

public class FileServiceImplTest {
    private final String REMOTE_IP = "127.0.0.1";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String testLocalFile = System.getProperty("user.dir") + "\\src\\test\\java\\com\\maoding\\FileServer\\upload_test.txt";
    private static final String testLocalLargeFile = System.getProperty("user.dir") + "\\src\\test\\java\\com\\maoding\\FileServer\\卯丁协同设计用户操作手册.docx";
    private static final String testDir = "testForFileService";

    private SimpleNodeDTO lastNode = null;
    private Boolean lastIsLocal = null;
    private Map<SimpleNodeDTO,Boolean> lastNodeMap = new HashMap<>();

    @Autowired
    IceConfig iceConfig;

    @Autowired
    private FileService localService;

    private FileServicePrx remote = null;

    private FileServicePrx getRemote(String ip) {
        if (remote == null) {
            FileServicePrxImpl prx = new FileServicePrxImpl();
            remote = prx.getServicePrx("FileService@FileServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h " + ip + " -p 4061",
                    FileServicePrx.class,_FileServicePrxI.class);
        }
        return remote;
    }
    private FileServicePrx getRemote(){
        return getRemote(REMOTE_IP);
    }


    @Test
    public void testListAllNode() throws Exception {
        List<SimpleNodeDTO> list;
        if (getIsLocal(REMOTE_IP)) {
            list = localService.listAllNodeForAccount(getLocalAccount(),null);
        } else {
            list = getRemote().listAllNodeForAccount(getRemoteAccount());
        }
        assert (list.size() > 0);
    }

    @Test
    public void testCreateTaskDir() throws Exception{
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(true);
        request.setFullName("Dir_" + StringUtils.getTimeStamp());
        SimpleNodeDTO node;
        for (int i=0; i<1; i++) {
            if (getIsLocal(REMOTE_IP)) {
                node = localService.createNodeForAccount(getLocalAccount(), getLocalTask(), request, null);
                saveLast(node,true);
            } else {
                node = getRemote().createNodeForAccount(getRemoteAccount(), getRemoteTask(), request);
                saveLast(node,false);
            }
            assert (node != null);
        }
        deleteLast();
    }

    private boolean getIsLocal(String ip) {
        return StringUtils.isEmpty(ip);
    }

    @Test
    @Ignore
    public void testClearAll() throws Exception {
        long t = System.currentTimeMillis();
        //*//本行以单斜杠开始则执行远端测试，双斜杠则执行本地测试
            localService.clearAll(getLocalAccount(),null);
        /*///下面是远端测试代码
            getRemote().clearAll(getRemoteAccount());
        //*/
        log.info("\t>>>>>>>> testClearAll:" + (System.currentTimeMillis()-t) + "ms");
    }

    @Test
    @Ignore
    public void testSetWebRoleStatus() throws Exception {
        localService.setWebRoleStatus(getLocalWebRole(),"1",null);
    }

    private WebRoleDTO getLocalWebRole() throws Exception {
        return localService.getWebRole(getLocalAccount(),getLocalFileNode(),null);
    }
    @Test
    @Ignore
    public void testCreateAnnotate() throws Exception {
        AnnotateRequestDTO request = new AnnotateRequestDTO();
        request.setData(new byte[]{0x33,0x34,0x35});
        localService.createAnnotateCheck(getLocalAccount(),getLocalFileNode(),request,null);
    }

    @Test
    @Ignore
    public void testListWebArchive() throws Exception {
        List<SimpleNodeDTO> list;
        list = listWebArchive();

    }

    private List<SimpleNodeDTO> listWebArchive() throws Exception {
        log.debug("\t>>>>>>>> listWebArchive");
        return localService.listWebArchiveDirForAccount(getLocalAccount(),null,null);
    }

    @Test
    @Ignore
    public void testListWebRoleTask() throws Exception {
        List<WebRoleDTO> list = localService.listWebRoleTask(getLocalAccount(),null);
    }

    @Test
    @Ignore
    public void testDeleteDir() throws Exception {
        CreateNodeRequestDTO dirCreate = new CreateNodeRequestDTO();
        dirCreate.setIsDirectory(true);
        dirCreate.setFullName("TaskDirForDelete");
        CreateNodeRequestDTO fileCreate = new CreateNodeRequestDTO();
        fileCreate.setIsDirectory(false);
        fileCreate.setFullName("TaskDirChildForDelete.text");
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
//                SimpleNodeDTO dirNode = localService.createNodeForAccount(getLocalAccount(), getLocalTask(),dirCreate,null);
//                SimpleNodeDTO fileNode = localService.createNodeForAccount(getLocalAccount(), dirNode,fileCreate,null);
                SimpleNodeDTO dirNode = getLocalNode("F271E5C3901C4C99A69611881811F6CB-1",false);
                localService.deleteNodeForAccount(getLocalAccount(),dirNode,null);
                localService.deleteNodeForAccount(getLocalAccount(),dirNode,null);
            } else {
                SimpleNodeDTO dirNode = getRemote().createNodeForAccount(getLocalAccount(), getLocalTask(),dirCreate);
                SimpleNodeDTO fileNode = getRemote().createNodeForAccount(getLocalAccount(), dirNode,fileCreate);
                getRemote().deleteNodeForAccount(getRemoteAccount(),dirNode,null);
                getRemote().deleteNodeForAccount(getRemoteAccount(),dirNode,null);
            }
            log.info("\t>>>>>>>> deleteLocalDir:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testDeleteFile() throws Exception {
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFullName("TaskFileForDelete.txt");
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                SimpleNodeDTO node = localService.createNodeForAccount(getLocalAccount(), getLocalTask(),request,null);
                localService.deleteNodeForAccount(getLocalAccount(),node,null);
                localService.deleteNodeForAccount(getLocalAccount(),node,null);
            } else {
                SimpleNodeDTO node = getRemote().createNodeForAccount(getRemoteAccount(), getRemoteTask(),request,null);
                getRemote().deleteNodeForAccount(getRemoteAccount(),node,null);
                getRemote().deleteNodeForAccount(getRemoteAccount(),node,null);
            }
            log.info("\t>>>>>>>> testDeleteFile:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testUpdateVersion() throws Exception {
        updateVersion();
    }

    private SimpleNodeDTO updateVersion() throws Exception {
        log.debug("\t>>>>>>>> updateVersion");
        CommitRequestDTO request = new CommitRequestDTO();
        request.setFileVersion("v2.0");
        return localService.updateVersion(getLocalAccount(),getLocalFileNode(),getLocalFileNode(),request,null);
    }


    @Test
    @Ignore
    public void testCreateVersion() throws Exception {
        log.debug("\t>>>>>>>> testCreateVersion");
        CommitRequestDTO request = new CommitRequestDTO();
        request.setFileVersion("v1.0");
        SimpleNodeDTO node = localService.createVersion(getLocalAccount(),getLocalFileNode(),"a.txt",request,null);
    }

    private MoveNodeRequestDTO getMoveDirRequest() throws Exception{
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("cde");
        return request;
    }

    private MoveNodeRequestDTO getMoveFileRequest() throws Exception{
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("x.dwg");
        return request;
    }

    @Test
    @Ignore
    public void testMoveFile() throws Exception {
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("abc/xxx.txt");
        SimpleNodeDTO node;
        for (int i=0; i<1; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                node = localService.moveNodeForAccount(getLocalAccount(), getLocalFileNode(),getLocalTask(),request,null);
            } else {
                node = getRemote().moveNodeForAccount(getRemoteAccount(), getRemoteFileNode(), null, request);
            }
            log.info("\t>>>>>>>> testMoveFile:" + (System.currentTimeMillis() - t) + "ms");
        }
    }

    private SimpleNodeDTO moveRemoteDir() throws Exception {
        log.debug("\t>>>>>>>> moveRemoteDir");
        return getRemote().moveNodeForAccount(getRemoteAccount(), getRemoteDirNode(),getRemoteTask(),getMoveDirRequest());
    }

    private SimpleNodeDTO changeRemoteDir() throws Exception {
        log.debug("\t>>>>>>>> changeRemoteDir");
        return getRemote().moveNodeForAccount(getRemoteAccount(), getRemoteDirNode(),null,getMoveDirRequest());
    }

    private SimpleNodeDTO moveDir() throws Exception {
        log.debug("\t>>>>>>>> moveDir");
        return localService.moveNodeForAccount(getLocalAccount(), getLocalDirNode(),getLocalTask(),getMoveDirRequest(),null);
    }

    private SimpleNodeDTO changeDirName() throws Exception {
        log.debug("\t>>>>>>>> changeDirName");
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("abcde");
        return localService.moveNodeForAccount(getLocalAccount(), getLocalDirNode(),null,request,null);
    }

    private SimpleNodeDTO changeFileName() throws Exception {
        log.debug("\t>>>>>>>> changeFileName");
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("axax.txt");
        return localService.moveNodeForAccount(getLocalAccount(),getLocalFileNode(), getLocalDirNode(),request,null);
    }

    private SimpleNodeDTO moveFile() throws Exception {
        log.debug("\t>>>>>>>> moveFile");
        MoveNodeRequestDTO request = new MoveNodeRequestDTO();
        request.setFullName("x/y/aaaa.txt");
        return localService.moveNodeForAccount(getLocalAccount(),getLocalFileNode(), getLocalDirNode(),request,null);
    }

    private NodeFileDTO getLocalFile() throws Exception {
        return getFile("2DA6CBCD6F2A45DFBC8D89DD6029D26E",true);
    }

    private NodeFileDTO getRemoteFile() throws Exception {
        return getFile("2DA6CBCD6F2A45DFBC8D89DD6029D26E",false);
    }

    private NodeFileDTO getFile(String id,boolean isLocal) throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setFileId(id);
        List<NodeFileDTO> fileList;
        fileList = getStoragePrx(isLocal).listFile(query);
        return ObjectUtils.getFirst(fileList);
    }

    private StorageServicePrx getStoragePrx(boolean isLocal) throws Exception {
        StorageServicePrx prx;
        if (isLocal) {
            prx = new StorageServicePrxImpl();
        } else {
            CoreRemoteService<StorageServicePrx> imp = new CoreRemoteService<>();
            prx = imp.getServicePrx("StorageService@StorageServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h 192.168.13.140 -p 4061",
                    StorageServicePrx.class,_StorageServicePrxI.class);
        }
        return prx;
    }

    @Test
    @Ignore
    public void testAddAccessory() throws Exception{
        addAccessorySimple();
        addAccessoryWithData();
    }

    private NodeFileDTO addAccessoryWithData() throws Exception{
        log.debug("\t>>>>>>>> addAccessoryWithData");
        AccessoryRequestDTO request = new AccessoryRequestDTO();
        request.setPath("a/b/c.txt");
        request.setData(new byte[]{1,2,3});
//        return localService.createAccessory(getLocalAccount(),request,null);
        return null;
    }

    private NodeFileDTO addAccessorySimple() throws Exception{
        log.debug("\t>>>>>>>> addAccessorySimple");
        AccessoryRequestDTO request = new AccessoryRequestDTO();
        request.setPath("a/b/c.txt");
//        return localService.addAccessory(getLocalAccount(),request,null);
        return null;
    }

    @Test
    @Ignore
    public void testListCANode() throws Exception {
        List<CANodeDTO> list;
        for (int i=0; i<1; i++){
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                list = localService.listCANode(getLocalAccount(), null);
            } else {
                list = getRemote().listCANode(getRemoteAccount());
            }
            log.info("\t>>>>>>>> testListCANode:" + (System.currentTimeMillis()-t) + "ms");
        }
    }

    @Test
    @Ignore
    public void testListDesignNode() throws Exception {
        List<CANodeDTO> list;
        for (int i=0; i<1; i++){
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                list = localService.listDesignNode(getLocalAccount(), null);
            } else {
                list = getRemote().listDesignNode(getRemoteAccount());
            }
            log.info("\t>>>>>>>> testListDesignNode:" + (System.currentTimeMillis()-t) + "ms");
        }
    }

    private QueryNodeDTO getQueryTaskChild() throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setPid(getLocalTask().getId());
        return query;
    }

    private QueryNodeDTO getQueryTaskChildren() throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setParentPath(getLocalTask().getPath());
        return query;
    }

    @Test
    @Ignore
    public void listNotRoleNode() throws Exception {
        List<SimpleNodeDTO> list;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                list = localService.listChildNodeForAccount(getLocalAccount(),getLocalTask(),null);
            } else {
                list = getRemote().listChildNodeForAccount(getRemoteAccount(),getRemoteCATask());
            }
            log.info("\t>>>>>>>> testListDesignNode:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    private SimpleNodeDTO getRemoteByPath() throws Exception {
        log.debug("\t>>>>>>>> getRemoteByPath");
        return getRemote().getNodeByPathForAccount(getRemoteAccount(),"/卯丁软件开发/设计/三月份大家一起努力吧！/新建 Microsoft Word 文档.docx");
    }

    @Test
    @Ignore
    public void testListRemoteAll() throws Exception {
        log.debug("\t>>>>>>>> testListRemoteAll");

    }

    @Test
    @Ignore
    public void testListChild() throws Exception {
        List<SimpleNodeDTO> list;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                list = localService.listChildNodeForAccount(getLocalAccount(),getLocalRange(),null);

                assert(list.size() > 0);
            } else {
                list = getRemote().listChildNodeForAccount(getRemoteAccount(),getRemoteRange());

                list = getRemote().listAllNodeForAccount(getRemoteAccount());
                List<SimpleNodeDTO> dstList = new ArrayList<>();
                for (SimpleNodeDTO node : list){
                    if (StringUtils.isSame("ddb7d626b9a1403893b8e39347478453-1",node.getPid())){
                        dstList.add(node);
                    }
                }
                assert(dstList.size() > 0);
            }
            log.info("\t>>>>>>>> testListChild:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    private List<SimpleNodeDTO> listRemoteRoot() throws Exception {
        log.debug("\t>>>>>>>> listRemoteRoot");
        return getRemote().listRootNodeForAccount(getRemoteAccount());
    }

    private List<SimpleNodeDTO> listLocalRoot() throws Exception {
        log.debug("\t>>>>>>>> listLocalRoot");
        return localService.listRootNodeForAccount(getRemoteAccount(),null);
    }


    private List<SimpleNodeDTO> listChildrenNode() throws Exception {
        log.debug("\t>>>>>>>> listChildrenNode");
        return localService.listChildrenNodeForAccount(getLocalAccount(), getLocalTask(),null);
    }

    private List<SimpleNodeDTO> listChildNode() throws Exception {
        log.debug("\t>>>>>>>> listChild");
        return localService.listChildNodeForAccount(getLocalAccount(), getLocalTaskParent(),null);
    }

    private List<SimpleNodeDTO> listRootNode() throws Exception {
        log.debug("\t>>>>>>>> listRootNode");
        return localService.listRootNodeForAccount(getLocalAccount(), null);
    }

    private AccountDTO getRemoteAccount(){
        return getAccount("07649b3d23094f28bfce78930bf4d4ac");
    }

    private AccountDTO getLocalAccount(){
        return getAccount("d437448683314cad91dc30b68879901d");
//        return getAccount("07649b3d23094f28bfce78930bf4d4ac");
    }

    private AccountDTO getAccount(String id){
        AccountDTO account = new AccountDTO();
        account.setId(id);
        if (StringUtils.isSame(id,"d437448683314cad91dc30b68879901d")){
            account.setName("张成亮");
        } else if (StringUtils.isSame(id,"d437448683314cad91dc30b68879901d")) {
            account.setName("卢昕");
        }
        return account;
    }

    private SimpleNodeDTO getLocalNode(String id,boolean isFuzzy) throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        if (isFuzzy) {
            query.setFuzzyId(StringUtils.left(id, StringUtils.DEFAULT_ID_LENGTH));
        } else {
            query.setId(id);
        }
        return getNode(query,true);
    }

    private SimpleNodeDTO getRemoteNode(String id,boolean isFuzzy) throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        if (isFuzzy) {
            query.setFuzzyId(StringUtils.left(id, StringUtils.DEFAULT_ID_LENGTH));
        } else {
            query.setId(id);
        }
        return getNode(query,false);
    }

    private SimpleNodeDTO getNode(QueryNodeDTO query,boolean isLocal) throws Exception {
        List<SimpleNodeDTO> list;
        if (isLocal) {
            list = localService.listNodeForAccount(getLocalAccount(), query, null);
        } else {
            list = getRemote().listNodeForAccount(getLocalAccount(), query);
        }
        return list.get(0);
    }

    private SimpleNodeDTO getLocalDirNode() throws Exception {
        return getLocalNode("2D4DEF1EDD21401D95848A1A1ED3910C-3",false);
    }

    private SimpleNodeDTO getLocalTaskParent() throws Exception {
        return getLocalNode("552f9b02dc2d4e9c9ee9a9ce402b7e07-1",false);
    }

    private SimpleNodeDTO getLocalTask() throws Exception {
        return getNode("0942c136129d45349e0eb238e8b5ebbe-1",true,false);
    }

    private SimpleNodeDTO getRemoteTask() throws Exception {
        return getNode("728dc784ed4145628ee4a5f85ee811a8-1",false,false);
    }

    private SimpleNodeDTO getRemoteRange() throws Exception {
        return getNode("ddb7d626b9a1403893b8e39347478453-1",false,false);
    }

    private SimpleNodeDTO getLocalRange() throws Exception {
        return getNode("ddb7d626b9a1403893b8e39347478453-1",true,false);
    }

    private SimpleNodeDTO getRemoteCATask() throws Exception {
        return getNode("2963f576c6dc46bba0624153ce53c308-2",false,false);
    }

    private SimpleNodeDTO getRemoteDirNode() throws Exception {
        return getNode("3D044277618A472C9F2DCF81E62194D9-1",false,false);
    }

    private SimpleNodeDTO getRemoteFileNode() throws Exception {
        return getNode("0417A569A3B04C9EAF02191A355A071D-1",false,false);
    }

    private SimpleNodeDTO getLocalFileNode() throws Exception {
        return getLocalNode("4A5B0B122331489B98BFE847B9B788AC-1",false);
    }

    private List<SimpleNodeDTO> getLocalFileNodeList() throws Exception {
        List<SimpleNodeDTO> nodeList = new ArrayList<>();
        nodeList.add(getLocalNode("4A5B0B122331489B98BFE847B9B788AC-1",true));
        return nodeList;
    }

    private List<SimpleNodeDTO> getRemoteFileNodeList() throws Exception {
        List<SimpleNodeDTO> nodeList = new ArrayList<>();
        nodeList.add(getRemoteNode("2544BB1F7CA34A9DB134F3EA50796D33-1",true));
        return nodeList;
    }


    private SimpleNodeDTO getRemoteNode() throws Exception {
        return getRemoteNode("4EC9E8A5DCED400A83D4F09C8DD7F6CB-1");
    }

    private SimpleNodeDTO getRemoteNode(String id) throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        query.setId(id);
        List<SimpleNodeDTO> list = getRemote().listNodeForAccount(getRemoteAccount(),query);
        return list.get(0);
    }

    private FullNodeDTO getLocalFullNode() throws Exception {
        return localService.getFullNodeForAccount(getLocalAccount(),getLocalFileNode(),null);
    }

    @Test
    @Ignore
    public void testReadNode() throws Exception {
        FileDataDTO data;
        for (int i=0; i<1; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                data = localService.readNodeForAccount(getLocalAccount(),getLocalFileNode(),0,0,null);
            } else {
                data = getRemote().readNodeForAccount(getRemoteAccount(), getRemoteFileNode(), 0, 0);
            }
            log.info("\t>>>>>>>> testReadNode:" + (System.currentTimeMillis() - t) + "ms");
        }
    }

    @Test
    @Ignore
    public void testReadFile() throws Exception {
        FileDataDTO data;
        for (int i=0; i<1; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                data = localService.readFile(getLocalAccount(), getLocalFile(),0,0,null);
            } else {
                data = getRemote().readFile(getRemoteAccount(), getRemoteFile(), 0, 0);
            }
            log.info("\t>>>>>>>> testReadFile:" + (System.currentTimeMillis() - t) + "ms" );
        }
    }

    @Test
    @Ignore
    public void testLogin() throws Exception {
        boolean b;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++) {
            /*//本行以单斜杠开始则执行远端测试，双斜杠则执行本地测试
                b = localService.login(getLocalAccount(),getLocalFileNode(),0,0,null);
                log.info("\t>>>>>>>> testLogin:" + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
            /*///下面是远端测试代码
                b = getRemote().login(getRemoteLoginInfo());
                log.info("\t>>>>>>>> testLogin:" + (System.currentTimeMillis() - t) + "ms");t = System.currentTimeMillis();
            //*/
        }
    }

    private boolean loginLocal() throws Exception {
        log.debug("\t>>>>>>>> login");
        return localService.login(getLocalLoginInfo(),null);
    }


    private LoginDTO getLocalLoginInfo() {
        return new LoginDTO(
                "",
                "",
                false,
                "123456",
                "13680809727");
    }

    private LoginDTO getRemoteLoginInfo() {
        return new LoginDTO(
                "",
                "",
                false,
                "123456",
                "13680809727");
    }


    @Test
    @Ignore
    public void testAuditCANode() throws Exception {
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                CANodeDTO caNode = getLocalCANode();
                caNode.setIsPassAudit(!caNode.getIsPassAudit());
                localService.auditNodeRequestForAccount(getLocalAccount(), caNode,null);
            } else {
                CANodeDTO caNode = getRemoteCANode();
                caNode.setIsPassAudit(!caNode.getIsPassAudit());
                getRemote().askCANodeRequestForAccount(getRemoteAccount(), caNode);
            }
            log.info("\t>>>>>>>> testAuditCANode:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testAskCA() throws Exception {
        SimpleNodeDTO node;
        for (int i=0; i<1; i++){
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                node = localService.askCANodeRequestForAccount(getLocalAccount(), getLocalDesignNode(),null);
            } else {
                node = getRemote().askCANodeRequestForAccount(getRemoteAccount(), getRemoteDesignNode());
            }
            log.info("\t>>>>>>>> testAskCA:" + (System.currentTimeMillis()-t) + "ms");
        }
    }

    private SimpleNodeDTO askLocalCA() throws Exception {
        return localService.askCANodeRequestForAccount(getLocalAccount(), getLocalDesignNode(),null);
    }

    private CANodeDTO getLocalCANode() throws Exception {
        return getCANode("F6CD4843A3F94ADBB6CA10F0965890F8",true);
    }

    private CANodeDTO getRemoteCANode() throws Exception {
        return getCANode("4A5B0B122331489B98BFE847B9B788AC",false);
    }

    private CANodeDTO getLocalDesignNode() throws Exception {
        return getDesignNode("5B6E42C97CB242C8805B4305A5149101",true);
    }

    private List<CANodeDTO> getLocalDesignNodeList() throws Exception {
        List<CANodeDTO> list = new ArrayList<>();
        list.add(getDesignNode("4A5B0B122331489B98BFE847B9B788AC",true));
        return list;
    }

    private List<CANodeDTO> getRemoteDesignNodeList() throws Exception {
        List<CANodeDTO> list = new ArrayList<>();
        list.add(getDesignNode("DA82F7D2555C442797E298001379C0EE",false));
        return list;
    }

    private CANodeDTO getRemoteDesignNode() throws Exception {
        List<CANodeDTO> list = getRemote().listDesignNode(getRemoteAccount());
        return getDesignNode("DA82F7D2555C442797E298001379C0EE",false);
    }

    private CANodeDTO getDesignNode(String id,boolean isLocal) throws Exception {
        return getCANode(id,true,isLocal);
    }

    private CANodeDTO getCANode(String id,boolean isLocal) throws Exception {
        return getCANode(id,false,isLocal);
    }

    private CANodeDTO getCANode(String id,boolean isDesign,boolean isLocal) throws Exception {
        List<CANodeDTO> list;
        if (isLocal){
            if (isDesign){
                list = localService.listDesignNode(getLocalAccount(),null);
            } else {
                list = localService.listCANode(getLocalAccount(),null);
            }
        } else {
            if (isDesign){
                list = getRemote().listDesignNode(getRemoteAccount());
            } else {
                list = getRemote().listCANode(getRemoteAccount());
            }
        }
        CANodeDTO caNode = null;
        for (CANodeDTO ca : list){
            if (StringUtils.isSame(StringUtils.left(id,StringUtils.DEFAULT_ID_LENGTH),ca.getId())){
                caNode = ca;
                break;
            }
        }
        if (!isValid(caNode)){
            log.error("没找到CANode");
        }
        return caNode;
    }

    private boolean isValid(CANodeDTO ca){
        return (ca != null) && (StringUtils.isNotEmpty(ca.getId()));
    }


    @Test
    @Ignore
    public void testSummaryFile() throws Exception {
        QuerySummarySimpleDTO query = new QuerySummarySimpleDTO();

        SummaryFileDTO result;
        if (getIsLocal(REMOTE_IP)) {
            query.setAccountId(getLocalAccount().getId());
            result = localService.summaryFile(query,null);
        } else {
            query.setAccountId(getRemoteAccount().getId());
            result = getRemote().summaryFile(query,null);
        }
    }

    @Test
    @Ignore
    public void testIssueNode() throws Exception {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setOwnerUserId("123");
        request.setMajorName("建筑");
        request.setFileVersion("v3.0");
        SimpleNodeDTO node;
        long t = System.currentTimeMillis();
        if (getIsLocal(REMOTE_IP)) {
            request.setPid(getRemoteSkyParent().getId());
            node = localService.issueNodeForAccount(getLocalAccount(),getLocalFileNode(),request,null);
        } else {
            request.setPid(getRemoteSkyParent().getId());
            node = getRemote().issueNodeForAccount(getRemoteAccount(),getRemoteFileNode(),request,null);
        }
        log.info("\t>>>>>>>> testIssueNode:" + (System.currentTimeMillis()-t) + "ms");
    }

    private SimpleNodeDTO getLocalSkyParent() throws Exception {
        return getSkyNode("29f323c5072d4341a21bae6927cd3b46",true);
    }

    private SimpleNodeDTO getRemoteSkyParent() throws Exception {
        return getSkyNode("29f323c5072d4341a21bae6927cd3b46",false);
    }

    private SimpleNodeDTO getSkyNode(String id, boolean isLocal) throws Exception {
        SimpleNodeDTO node = null;
        List<SimpleNodeDTO> list;
        if (isLocal) {
            list = localService.listWebArchiveDirForAccount(getLocalAccount(),null,null);
        } else {
            list = getRemote().listWebArchiveDirForAccount(getRemoteAccount(),null,null);
        }
        for (SimpleNodeDTO n : list){
            if (StringUtils.isSame(id,n.getId())){
                node = n;
                break;
            }
        }
        return node;
    }

    @Test
    @Ignore
    public void checkNode() throws Exception {
        SimpleNodeDTO node;
        CANodeDTO caNode = getLocalDesignNode();
        caNode.setIsPassCheck(!caNode.getIsPassCheck());
        node = localService.checkNodeRequestForAccount(getLocalAccount(),caNode,null);
    }

    @Test
    @Ignore
    public void testCommitNode() throws Exception {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setOwnerUserId("123");
        request.setMajorName("建筑");
        request.setFileVersion("v3.0");
        SimpleNodeDTO node;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                node = localService.commitNodeForAccount(getLocalAccount(), getLocalFileNode(), request, null);
            } else {
                node = getRemote().commitNodeForAccount(getRemoteAccount(), getRemoteFileNode(), request);
            }
            log.info("\t>>>>>>>> testReadNode:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testCommitCANodeList() throws Exception {
        CommitListResultDTO result;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                result = localService.requestCommitListForAccount(getLocalAccount(),getLocalDesignNodeList(), null);
            } else {
                result = getRemote().requestCommitListForAccount(getRemoteAccount(), getRemoteDesignNodeList());
            }
            log.info("\t>>>>>>>> testCommitCANodeList:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testCommitList() throws Exception {
        CommitRequestDTO request = new CommitRequestDTO();
        request.setOwnerUserId("123");
        request.setMajorName("建筑");
        request.setFileVersion("v3.0");
        CommitListResultDTO result;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            if (getIsLocal(REMOTE_IP)) {
                result = localService.commitNodeListForAccount(getLocalAccount(), getLocalFileNodeList(), request, null);
            } else {
                result = getRemote().commitNodeListForAccount(getRemoteAccount(), getRemoteFileNodeList(), request);
            }
            log.info("\t>>>>>>>> testCommitList:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }

    @Test
    @Ignore
    public void testGetWebRole() throws Exception {
        getWebRole();
    }

    private WebRoleDTO getWebRole() throws Exception {
        log.debug("\t>>>>>>>> getWebRole");
        return  localService.getWebRole(getLocalAccount(),getLocalFileNode(),null);
    }


    @Ignore
    @Test
    public void testReleaseNodeWithLength() throws Exception {
        final int FILE_LENGTH = 300;
        long t = System.currentTimeMillis();
        if (getIsLocal(REMOTE_IP)) {
            localService.releaseNodeForAccount(getLocalAccount(), getLocalFileNode(), FILE_LENGTH, null);
        } else {
            getRemote().releaseNodeForAccount(getRemoteAccount(), getRemoteFileNode(), FILE_LENGTH,null);
        }
        log.info("\t>>>>>>>> testReleaseNodeWithLength:" + (System.currentTimeMillis()-t) + "ms");
    }

    @Ignore
    @Test
    public void testReleaseNewNode() throws Exception {
        for (int i=0; i<1; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                localService.releaseNodeForAccount(getLocalAccount(), getLocalNewFileNode(), 0, null);
            } else {
                getRemote().releaseNodeForAccount(getRemoteAccount(), getRemoteNewFileNode(), 0, null);
            }
            log.info("\t>>>>>>>> testReleaseNewNode:" + (System.currentTimeMillis() - t) + "ms");
//            deleteLastNode();
        }
    }

    @Test
    @Ignore
    public void testSetNewNodeLength() throws Exception {
        final int FILE_LENGTH = 150;
        for (int i=0; i<2; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                localService.setNodeLengthForAccount(getLocalAccount(), getLocalEmptyNewFileNode(), FILE_LENGTH + i, null);
            } else {
                getRemote().setNodeLengthForAccount(getRemoteAccount(), getRemoteEmptyNewFileNode(), FILE_LENGTH + i, null);
            }
            log.info("\t>>>>>>>> testSetNewNodeLength:" + (System.currentTimeMillis() - t) + "ms");
            deleteLastNode();
        }
    }

    @Test
    @Ignore
    public void testReleaseNode() throws Exception {
        for (int i=0; i<1; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                localService.releaseNodeForAccount(getLocalAccount(), getLocalFileNode(), 0, null);
            } else {
                getRemote().releaseNodeForAccount(getRemoteAccount(), getRemoteFileNode(), 0);
            }
            log.info("\t>>>>>>>> testReleaseNode:" + (System.currentTimeMillis()-t) + "ms");
        }
    }

    @Test
    @Ignore
    public void testReleaseFile() throws Exception {
        log.debug("\t>>>>>>>> testReleaseFile");
        localService.releaseFile(getLocalAccount(), getLocalFile(),"a/b/c.txt",null);
    }

    @Test
    @Ignore
    public void testReloadNode() throws Exception {
        log.debug("\t>>>>>>>> testReloadNode");
        long t = System.currentTimeMillis();
        localService.reloadNodeForAccount(getLocalAccount(),getLocalFileNode(),null);
        log.info("\t>>>>>>>> testReloadNode:" + (System.currentTimeMillis()-t) + "ms");t = System.currentTimeMillis();
    }

    @Test
    @Ignore
    public void testWriteNode() throws Exception {
        int size = 11;
        long pos = 0;
        for (int times=0; times<10; times++){
            long t = System.currentTimeMillis();
            FileDataDTO fileData = createFileData(pos, size);
            if (getIsLocal(REMOTE_IP)) {
                pos += localService.writeNodeForAccount(getLocalAccount(), getLocalFileNode(), fileData, null);
            } else {
                pos += getRemote().writeNodeForAccount(getLocalAccount(), getRemoteFileNode(), fileData);
            }
            log.info("\t>>>>>>>> testWriteNode:" + (System.currentTimeMillis()-t) + "ms");
        }
    }

    private FileDataDTO createFileData(RandomAccessFile in, long pos, int size) throws Exception {
        //建立上传内容
        FileDataDTO fileData = new FileDataDTO();
        byte[] bytes = new byte[size];
        fileData.setPos(pos);
        in.seek(pos);
        fileData.setSize(in.read(bytes));
        fileData.setData(bytes);

        return fileData;
    }

    private FileDataDTO createFileData(long pos, int size) throws Exception {
        //建立上传内容
        byte[] bytes = new byte[size];
        char ch = '0';
        for (int i=0; i<size; i++){
            if (('0' <= ch) && (ch <= '9')) {
                bytes[i] = (byte) ch++;
            } else {
                bytes[i] = (byte) '\r';
                ch = '0';
            }
        }
        FileDataDTO fileData = new FileDataDTO();
        fileData.setPos(pos);
        fileData.setSize(size);
        fileData.setData(bytes);

        return fileData;
    }


    private CreateNodeRequestDTO getCreateFileRequest() throws Exception{
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFullName("aaaa.txt");
        return request;
    }

    @Test
    @Ignore
    public void testCreateTaskFile() throws Exception{
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFullName(StringUtils.getTimeStamp() + ".txt");
        SimpleNodeDTO node;
        for (int i=0; i<2; i++) {
            long t = System.currentTimeMillis();
            if (getIsLocal(REMOTE_IP)) {
                node = localService.createNodeForAccount(getLocalAccount(), getLocalTask(), request, null);
            } else {
                node = getRemote().createNodeForAccount(getRemoteAccount(), getRemoteTask(), request);
            }
            log.info("\t>>>>>>>> testCreateTaskFile:" + (System.currentTimeMillis() - t) + "ms");
        }
    }


    @Test
    @Ignore
    public void testCreateStorageFile() throws Exception{
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setFullName("StorageFile.txt");
        request.setIsDirectory(false);
        request.setFileLength(100);
        SimpleNodeDTO node;
        long t = System.currentTimeMillis();
        for (int i=0; i<1; i++){
            /*//本行以单斜杠开始则执行远端测试，双斜杠则执行本地测试
                node = localService.createNodeForAccount(getLocalAccount(), getLocalDirNode(),request,null);
            /*///下面是远端测试代码
                node = getRemote().createNodeForAccount(getRemoteAccount(), getRemoteDirNode(),request);
            //*/
            log.info("\t>>>>>>>> testCreateStorageFile:" + (System.currentTimeMillis()-t) + "ms");
            t = System.currentTimeMillis();
        }
    }


    private SimpleNodeDTO createRangeChild() throws Exception {
        SimpleNodeDTO parent = getLocalSimpleNode("d1ba184a668d49789a224e8e8200fb17-1");
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFileLength(10);
        request.setFullName("aaaa.txt");
        SimpleNodeDTO node = localService.createNodeForAccount(getLocalAccount(), parent,request,null);
        return node;
    }

    private SimpleNodeDTO getLocalSimpleNode(String id) throws Exception {
        return localService.getNodeByIdForAccount(getLocalAccount(),id,null);
    }

    private SimpleNodeDTO getNode(String id,boolean isLocal,boolean isFuzzy) throws Exception {
        QueryNodeDTO query = new QueryNodeDTO();
        if (isFuzzy) {
            query.setFuzzyId(StringUtils.left(id, StringUtils.DEFAULT_ID_LENGTH));
        } else {
            query.setId(id);
        }
        List<SimpleNodeDTO> list = (isLocal) ? localService.listNodeForAccount(getLocalAccount(),query,null) :
                getRemote().listNodeForAccount(getLocalAccount(),query);
        return list.get(0);
    }

    private SimpleNodeDTO createLocalLargeFile() throws Exception{
        log.debug("\t>>>>>>>> createLocalLargeFile");
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFullName("扩初jx-lm.dwg");
        return localService.createNodeForAccount(getLocalAccount(), getLocalTask(),request,null);
    }


    private SimpleNodeDTO createRemoteDirectory() throws Exception{
        log.debug("\t>>>>>>>> createRemoteDirectory");
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(true);
        request.setFullName("child3");
        return localService.createNodeForAccount(getRemoteAccount(), getRemoteTask(),request,null);
    }

    private SimpleNodeDTO createLocalFileWithSubDir() throws Exception{
        log.debug("\t>>>>>>>> createLocalFileWithSubDir");
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(false);
        request.setFileLength(10);
        request.setFullName("/father2/aaaa.txt");
        return localService.createNodeForAccount(getLocalAccount(), getLocalDirNode(),request,null);
    }

    @Test
    @Ignore
    public void testCreateLocalDirectory() throws Exception{
        log.debug("\t>>>>>>>> testCreateLocalDirectory");
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setIsDirectory(true);
        request.setFullName("father/child/child3");
        SimpleNodeDTO node;
        node = localService.createNodeForAccount(getLocalAccount(), getLocalTask(),request,null);
    }

    @Test
    @Ignore
    public void testGetNodeInfo() throws Exception {
        log.debug("\t>>>>>>>> testGetNodeInfo");
        QueryNodeInfoDTO query = new QueryNodeInfoDTO();
        QueryNodeInfoFileDTO fileQuery = new QueryNodeInfoFileDTO();
        query.setFileQuery(fileQuery);
        FullNodeDTO fullNode;
        fullNode = localService.getNodeInfoForAccount(getLocalAccount(),getLocalFileNode(),query,null);
    }

    private SimpleNodeDTO getLocalNewFileNode() throws Exception {
        return getNewFileNode(StringUtils.getTimeStamp() + ".txt",true,true);
    }

    private SimpleNodeDTO getRemoteNewFileNode() throws Exception {
        return getNewFileNode(StringUtils.getTimeStamp() + ".txt",false,true);
    }

    private SimpleNodeDTO getLocalEmptyNewFileNode() throws Exception {
        return getNewFileNode(StringUtils.getTimeStamp() + ".txt",true,false);
    }

    private SimpleNodeDTO getRemoteEmptyNewFileNode() throws Exception {
        return getNewFileNode(StringUtils.getTimeStamp() + ".txt",false,false);
    }

    private SimpleNodeDTO getNewFileNode(String name, boolean isLocal,boolean hasContent) throws Exception {
        CreateNodeRequestDTO request = new CreateNodeRequestDTO();
        request.setFullName(name);
        request.setIsDirectory(false);
        if (isLocal) {
            lastNode = localService.createNodeForAccount(getLocalAccount(),getLocalTask(),request,null);
        } else {
            lastNode = getRemote().createNodeForAccount(getLocalAccount(),getRemoteTask(),request,null);
        }
        if (hasContent) {
            writeContentToNode(lastNode, 100, isLocal);
        }
        lastIsLocal = isLocal;

        return lastNode;
    }

    private void writeContentToNode(SimpleNodeDTO node, int size, boolean isLocal) throws Exception {
        if (isLocal) {
            localService.writeNodeForAccount(getLocalAccount(),node,createFileData(0,size),null);
        } else {
            getRemote().writeNodeForAccount(getRemoteAccount(),node,createFileData(0,size),null);
        }
    }

    private void deleteLastNode() throws Exception {
        if ((lastIsLocal != null) && (lastNode != null)){
            if (lastIsLocal){
                localService.deleteNode(lastNode,null);
            } else {
                getRemote().deleteNode(lastNode,null);
            }
        }
    }

    private void saveLast(SimpleNodeDTO node,Boolean isLocal) throws Exception {
        lastNodeMap.put(node,isLocal);
    }

    private void deleteLast() throws Exception {
        for (Map.Entry<SimpleNodeDTO,Boolean> entry : lastNodeMap.entrySet()) {
            SimpleNodeDTO node = entry.getKey();
            boolean isLocal = entry.getValue();
            if (isLocal) {
                localService.deleteNodeForAccount(getLocalAccount(),node,null);
            } else {
                getRemote().deleteNodeForAccount(getRemoteAccount(),node);
            }
        }
        lastNodeMap.clear();
    }


}
