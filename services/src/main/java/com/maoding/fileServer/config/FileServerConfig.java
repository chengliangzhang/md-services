package com.maoding.fileServer.config;

import com.maoding.common.LocalConstService;
import com.maoding.common.config.WebServiceConfig;
import com.maoding.coreFileServer.CoreFileServer;
import com.maoding.coreFileServer.disk.DiskFileServer;
import com.maoding.coreFileServer.web.WebFileServer;
import com.maoding.coreUtils.DigitUtils;
import com.maoding.coreUtils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/1 12:09
 * 描    述 :
 */
@Component
@ConfigurationProperties(prefix = "fileServer")
public class FileServerConfig {
    private static final String DEFAULT_SERVER_TYPE_ID = Short.toString(LocalConstService.FILE_SERVER_TYPE_DISK);
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private static final String DEFAULT_BASE_DIR = "c:/work/file_server";
    private static final String DEFAULT_MIRROR_BASE_DIR = "c:/work/file_server/.mirror";

    @Autowired
    private WebServiceConfig webServiceConfig;

    private CoreFileServer localServer;

    private String serverTypeId;
    private String serverAddress;
    private String baseDir;
    private String mirrorBaseDir;

    public String getMirrorBaseDir() {
        return StringUtils.isEmpty(mirrorBaseDir) ? DEFAULT_MIRROR_BASE_DIR : mirrorBaseDir;
    }

    public void setMirrorBaseDir(String mirrorBaseDir) {
        this.mirrorBaseDir = mirrorBaseDir;
    }

    public String getServerTypeId(String serverTypeId) {
        return StringUtils.isEmpty(serverTypeId) ? getServerTypeId() : serverTypeId;
    }

    public String getServerTypeId() {
        return StringUtils.isEmpty(serverTypeId) ? DEFAULT_SERVER_TYPE_ID : serverTypeId;
    }

    public void setServerTypeId(String serverTypeId) {
        this.serverTypeId = serverTypeId;
    }

    public String getBaseDir(String serverTypeId,String baseDir) {
        serverTypeId = getServerTypeId(serverTypeId);
        if (StringUtils.isNotEmpty(baseDir)){
            return baseDir;
        } else if (isDiskServer(serverTypeId)) {
            return DEFAULT_BASE_DIR;
        } else if (isWebServer(serverTypeId)) {
            return webServiceConfig.getUpload();
        } else {
            return DEFAULT_BASE_DIR;
        }
    }

    public String getBaseDir() {
        return StringUtils.isEmpty(baseDir) ? getBaseDir(getServerTypeId(),null) : baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getServerAddress(String serverTypeId,String serverAddress) {
        serverTypeId = getServerTypeId(serverTypeId);
        if (StringUtils.isNotEmpty(serverAddress)){
            return serverAddress;
        } else if (isDiskServer(serverTypeId)) {
            return DEFAULT_SERVER_ADDRESS;
        } else if (isWebServer(serverTypeId)) {
            return webServiceConfig.getFileCenter();
        } else {
            return DEFAULT_SERVER_ADDRESS;
        }
    }

    public String getServerAddress() {
        return StringUtils.isEmpty(serverAddress) ? getServerAddress(getServerTypeId(),null) : serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isLocalServer(String serverTypeId,String serverAddress,String baseDir){
        return StringUtils.isSame(getServerTypeId(serverTypeId),getServerTypeId()) &&
                StringUtils.isSame(getServerAddress(serverTypeId,serverAddress),getServerAddress()) &&
                StringUtils.isSame(getBaseDir(serverTypeId,baseDir),getBaseDir());
    }
    private boolean isDiskServer(String serverTypeId){
        return LocalConstService.FILE_SERVER_TYPE_DISK == DigitUtils.parseShort(serverTypeId);
    }
    private boolean isWebServer(String serverTypeId){
        return LocalConstService.FILE_SERVER_TYPE_WEB == DigitUtils.parseShort(serverTypeId);
    }

    public CoreFileServer createCoreFileServer(@NotNull String serverTypeId){
        if (isDiskServer(serverTypeId)) {
            return new DiskFileServer();
        } else if (isWebServer(serverTypeId)) {
            return new WebFileServer();
        } else {
            return new PrxFileServer();
        }
    }

    public CoreFileServer getCoreFileServer(String serverTypeId,String serverAddress,String baseDir){
        serverTypeId = getServerTypeId(serverTypeId);
        serverAddress = getServerAddress(serverTypeId,serverAddress);
        baseDir = getBaseDir(serverTypeId,baseDir);
        CoreFileServer coreServer;
        if (isLocalServer(serverTypeId,serverAddress,baseDir)){
            if (localServer == null){
                localServer = createCoreFileServer(serverTypeId);
                localServer.coreSetServerAddress(serverAddress,baseDir);
            }
            coreServer = localServer;
        } else {
            coreServer = createCoreFileServer(serverTypeId);
            coreServer.coreSetServerAddress(serverAddress,baseDir,getMirrorBaseDir());
        }

        return coreServer;
    }
    public CoreFileServer getCoreFileServer(){
        return getCoreFileServer(getServerTypeId(),getServerAddress(),getBaseDir());
    }
}
