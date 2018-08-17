package com.maoding.common.servicePrx;

import com.maoding.common.zeroc.*;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/29 15:19
 * 描    述 :
 */
public class ConstServicePrxImpl extends CoreRemoteService<ConstServicePrx> implements ConstServicePrx {
    private static ConstServicePrx lastPrx = null;
    private static String lastConfig = null;
    private static ConstService localService = null;

    private ConstService getLocalService(){
        if (localService == null) {
            localService = SpringUtils.getBean(ConstService.class);
        }
        return localService;
    }

    public static ConstServicePrx getInstance(String config) {
        if ((lastPrx == null) || StringUtils.isNotSame(lastConfig,config)){
            ConstServicePrxImpl prx = new ConstServicePrxImpl();
            lastPrx = prx.getServicePrx(config, ConstServicePrx.class,  _ConstServicePrxI.class, prx);
            lastConfig = config;
        }
        return lastPrx;
    }

    @Deprecated
    public static ConstServicePrx getInstance() {
        return getInstance(null);
    }

    @Override
    public String getTitle(@NotNull ConstQuery query) throws CustomException {
        return getLocalService().getTitle(query,null);
    }

    @Override
    public String getExtra(@NotNull ConstQuery query) throws CustomException {
        return getLocalService().getExtra(query,null);
    }

    @Override
    public List<VersionDTO> listVersion(VersionQuery query) throws CustomException  {
        return getLocalService().listVersion(query,null);
    }
}
