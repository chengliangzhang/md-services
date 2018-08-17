package com.maoding.fileServer.config;

import com.maoding.common.servicePrx.FileServicePrxImpl;
import com.maoding.common.zeroc.CustomException;
import com.maoding.coreFileServer.CoreFileServer;
import com.maoding.coreUtils.StringUtils;
import com.maoding.fileServer.zeroc.FileServicePrx;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/3/15 19:23
 * 描    述 :
 */
public class PrxFileServer implements CoreFileServer{

    private FileServicePrx remote = null;
    private String service = null;
    private String config = null;

    @Override
    public String coreGetServerAddress() {
        return service;
    }

    @Override
    public void coreSetServerAddress(String serverAddress) {
        this.service = serverAddress;
    }

    @Override
    public String coreGetBaseDir() {
        return config;
    }

    @Override
    public void coreSetBaseDir(String baseDir) {
        this.config = baseDir;
    }

    private String getService(){
        return coreGetServerAddress();
    }

    private String getConfig(){
        return coreGetBaseDir();
    }

    private FileServicePrx getRemote() throws CustomException {
        if (remote == null) {
            remote = FileServicePrxImpl.getInstance((getService() + StringUtils.SPLIT_CONTENT + getConfig()),null);
        }
        return remote;
    }


}
