package com.maoding.common.servicePrx;

import com.maoding.common.zeroc.*;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.fileServer.zeroc.FileDataDTO;
import com.maoding.fileServer.zeroc.FileServicePrx;
import com.maoding.notice.zeroc.NoticeServicePrx;
import com.maoding.storage.zeroc.StorageServicePrx;
import com.maoding.user.zeroc.UserServicePrx;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/29 15:19
 * 描    述 :
 */
public class CommonServicePrxImpl extends CoreRemoteService<CommonServicePrx> implements CommonServicePrx {
    private static CommonServicePrx lastPrx = null;
    private static String lastConfig = null;
    private static CommonService localService = null;

    private CommonService getLocalService(){
        if (localService == null) {
            localService = SpringUtils.getBean(CommonService.class);
        }
        return localService;
    }

    public static CommonServicePrx getInstance(String config) {
        if ((lastPrx == null) || (StringUtils.isNotSame(lastConfig,config))){
            CommonServicePrxImpl prx = new CommonServicePrxImpl();
            lastPrx = prx.getServicePrx(config, CommonServicePrx.class, _CommonServicePrxI.class, prx);
            lastConfig = config;
        }
        return lastPrx;
    }

    @Override
    public ConstServicePrx getDefaultConstService() throws CustomException {
        return getLocalService().getDefaultConstService(null);
    }

    @Override
    public FileServicePrx getDefaultFileService() throws CustomException {
        return getLocalService().getDefaultFileService(null);
    }

    @Override
    public NoticeServicePrx getDefaultNoticeService() throws CustomException {
        return getLocalService().getDefaultNoticeService(null);
    }

    @Override
    public StorageServicePrx getDefaultStorageService() throws CustomException {
        return getLocalService().getDefaultStorageService(null);
    }

    @Override
    public UserServicePrx getDefaultUserService() throws CustomException {
        return getLocalService().getDefaultUserService(null);
    }

    @Override
    public VersionDTO getNewestClient() throws CustomException {
        return getLocalService().getNewestClient(null);
    }

    @Override
    public long getVersionLength(VersionDTO version) throws CustomException {
        return getLocalService().getVersionLength(version,null);
    }

    @Override
    public void updateService() throws CustomException {
        getLocalService().updateService(null);
    }

    @Override
    public FileDataDTO readVersion(VersionDTO version, long pos, int size) throws CustomException {
        return getLocalService().readVersion(version,pos,size,null);
    }
}
