package com.maoding.common;

import com.maoding.Application;
import com.maoding.common.config.BufferConfig;
import com.maoding.common.config.IceConfig;
import com.maoding.common.config.ProjectConfig;
import com.maoding.common.config.ScheduleConfig;
import com.maoding.common.servicePrx.*;
import com.maoding.common.zeroc.*;
import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreUtils.*;
import com.maoding.fileServer.zeroc.FileDataDTO;
import com.maoding.fileServer.zeroc.FileServicePrx;
import com.maoding.notice.zeroc.NoticeServicePrx;
import com.maoding.storage.zeroc.NodeFileDTO;
import com.maoding.storage.zeroc.StorageServicePrx;
import com.maoding.user.zeroc.UserServicePrx;
import com.zeroc.Ice.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/29 11:28
 * 描    述 :
 */
@Service("commonService")
public class CommonServiceImpl extends CoreLocalService implements CommonService{

    private static final String SERVICE_NAME = "maoding-services";
    private static final String CLIENT_NAME = "app";
    private static final String ONLY_ONE = "1";
    private static final String ICE_CONFIG_TITLE = "ice";
    private static final String ICE_SERVICE_CONST = "commonConst";
    private static final String ICE_SERVICE_STORAGE = "storage";
    private static final String ICE_SERVICE_FILE = "file";
    private static final String ICE_SERVICE_USER = "user";
    private static final String ICE_SERVICE_NOTICE = "notice";

    @Autowired
    private IceConfig iceConfig;

    @Autowired
    private BufferConfig bufferConfig;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private ScheduleConfig scheduleConfig;

    private String lastConstServiceConfig = null;

    @Override
    public NoticeServicePrx getDefaultNoticeService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        String config = getIceServiceConfig(ICE_SERVICE_NOTICE,current);
        NoticeServicePrx prx = NoticeServicePrxImpl.getInstance(config);

        TraceUtils.exit(log,t);
        return prx;
    }

    @Override
    public long getVersionLength(VersionDTO version, Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        NodeFileDTO file = getFileByVersion(version,current);
        long len = getDefaultFileService(current).getFileLength(file,true);

        TraceUtils.exit(log,t);
        return len;
    }

    @Override
    public FileDataDTO readVersion(VersionDTO version, long pos, int size, Current current) throws CustomException {
        //默认读取字节数
        final int defaultSize = 8192000;

        long t = TraceUtils.enter(log);

        NodeFileDTO file = getFileByVersion(version,current);
        FileDataDTO data = getDefaultFileService(current).readFile(null, file, pos, ((size > 0) ? size : defaultSize));

        TraceUtils.exit(log,t);
        return data;
    }

    @Override
    public StorageServicePrx getDefaultStorageService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        String config = getIceServiceConfig(ICE_SERVICE_STORAGE,current);
        StorageServicePrx prx = StorageServicePrxImpl.getInstance(config);

        TraceUtils.exit(log,t);
        return prx;
    }

    @Override
    public FileServicePrx getDefaultFileService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        String config = getIceServiceConfig(ICE_SERVICE_FILE,current);
        FileServicePrx prx = FileServicePrxImpl.getInstance(config,getDefaultStorageService(current));

        TraceUtils.exit(log,t);
        return prx;
    }

    @Override
    public UserServicePrx getDefaultUserService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        String config = getIceServiceConfig(ICE_SERVICE_USER,current);
        UserServicePrx prx = UserServicePrxImpl.getInstance(config);

        TraceUtils.exit(log,t);
        return prx;
    }

    @Override
    public ConstServicePrx getDefaultConstService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        String config = getIceServiceConfig(ICE_SERVICE_CONST,current);
        ConstServicePrx prx = getConstService(config,current);
        lastConstServiceConfig = config;

        TraceUtils.exit(log,t);
        return prx;
    }

    private ConstServicePrx getConstService(String config, Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        ConstServicePrx prx = ConstServicePrxImpl.getInstance(config);

        TraceUtils.exit(log,t);
        return prx;
    }

    @Override
    public void updateService(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        //获取当前版本
        String currentVersion = getCurrentServiceVersionName(current);
        //获取最新版本
        VersionDTO newestVersion = getNewestService(current);
        //如果最新版本比当前版本高
        if ((newestVersion != null) && StringUtils.isNotSame(currentVersion,newestVersion.getVersionName())){
            String url = newestVersion.getUpdateUrl();
            if (isFileServiceUrl(url)){
                //从文件服务器读取文件并保存到本地
                File updateFile = getUpdateFile();
                long len = getVersionLength(newestVersion,current);
                long pos = 0;
                while (pos < len) {
                    FileDataDTO data = readVersion(newestVersion,pos,0,current);
                    if ((data != null) && (data.getSize() > 0)){
                        FileUtils.writeDataToFile(updateFile,data.getPos(),data.getSize(),data.getData());
                        pos = data.getPos() + data.getSize();
                    } else {
                        pos = len;
                    }
                }
            }

            //调用升级脚本
            FileUtils.executeCmd(scheduleConfig.getUpdateConfig(),"自动升级");

            //退出应用
//            System.exit(0);
        }

        TraceUtils.exit(log,t);
    }

    private NodeFileDTO getFileByVersion(@NotNull VersionDTO version, Current current) throws CustomException {
        String url = version.getUpdateUrl();
        NodeFileDTO file = new NodeFileDTO();
        file.setServerTypeId(StringUtils.getContent(url,1));
        file.setServerAddress(StringUtils.getContent(url,2));
        file.setBaseDir(StringUtils.getContent(url,3));
        file.setReadOnlyKey(StringUtils.getContent(url,4));
        return file;
    }

    private File getUpdateFile() throws CustomException {
        String applicationName = null;
        try {
            applicationName = java.net.URLDecoder.decode(Application.class.getProtectionDomain().getCodeSource().getLocation().getFile(),"UTF-8");
            applicationName = StringUtils.formatPath(applicationName);
            if (applicationName.endsWith(StringUtils.SPLIT_PATH)){
                applicationName += Application.class.getName() + ".jar";
            }
        } catch (UnsupportedEncodingException e) {
            log.error("无法获取当前应用名称");
        }
        String updateFileName = StringUtils.appendPath(StringUtils.getDirName(applicationName),StringUtils.getFileNameWithoutExt(applicationName));
        updateFileName += "_update" + StringUtils.getFileExt(applicationName);
        return new File(updateFileName);
    }

    private boolean isFileServiceUrl(String s) {
        return (StringUtils.isNotEmpty(s)) && s.contains(StringUtils.SPLIT_CONTENT);
    }

    private String getCurrentServiceVersionName(Current current) throws CustomException {
        return projectConfig.getVersion();
    }

    private VersionDTO getCurrentService(Current current) throws CustomException {
        VersionQuery serviceQuery = new VersionQuery();
        serviceQuery.setSvnRepo(SERVICE_NAME);
        serviceQuery.setVersionName(getCurrentServiceVersionName(current));
        serviceQuery.setLimitRows(ONLY_ONE);
        List<VersionDTO> serviceList = getDefaultConstService(current).listVersion(serviceQuery);
        return ObjectUtils.getFirst(serviceList);
    }

    private VersionDTO getNewestService(Current current) throws CustomException {
        VersionQuery serviceQuery = new VersionQuery();
        serviceQuery.setSvnRepo(SERVICE_NAME);
        serviceQuery.setLimitRows(ONLY_ONE);
        List<VersionDTO> serviceList = getDefaultConstService(current).listVersion(serviceQuery);
        return ObjectUtils.getFirst(serviceList);
    }

    @Override
    public VersionDTO getNewestClient(Current current) throws CustomException {
        long t = TraceUtils.enter(log);

        //获取当前服务器版本
        VersionDTO service = getCurrentService(current);

        //获取客户端与当前服务器版本兼容的最新版本
        VersionQuery clientQuery = new VersionQuery();
        clientQuery.setSvnRepo(CLIENT_NAME);
        clientQuery.setLimitRows(ONLY_ONE);
        if (service != null){
            clientQuery.setServiceSvnVersion(service.getSvnVersion());
        }
        List<VersionDTO> clientList = getDefaultConstService(current).listVersion(clientQuery);
        VersionDTO client = ObjectUtils.getFirst(clientList);

        TraceUtils.exit(log,t,client);
        return client;
    }

    private String getIceServiceConfig(@NotNull String service,Current current) throws CustomException {
        final String suffix = ".IceService";
        final long aliveTime = 10000;

        String key = JsonUtils.obj2CleanJson(service) + suffix;
        List<String> list = bufferConfig.getListFromBuffer(String.class,key,aliveTime);
        if (ObjectUtils.isEmpty(list)){
            String title = service;
            String identify = iceConfig.getIdentify();
            if (StringUtils.isNotEmpty(identify)){
                title += "-" + identify;
            }
            ConstQuery query = new ConstQuery();
            query.setClassicName(ICE_CONFIG_TITLE);
            query.setTitle(title);
            String config = getConstService(lastConstServiceConfig,current).getExtra(query);
            if (StringUtils.isEmpty(config)){
                config = iceConfig.getProperty(service);
            }
            list = new ArrayList<>();
            list.add(config);
            bufferConfig.setListToBuffer(list,key,aliveTime);
        }

        return ObjectUtils.getFirst(list);
    }
}
