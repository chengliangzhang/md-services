package com.maoding.common.servicePrx;

import com.maoding.common.zeroc.CustomException;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.fileServer.zeroc.FileDataDTO;
import com.maoding.fileServer.zeroc.FileService;
import com.maoding.fileServer.zeroc.FileServicePrx;
import com.maoding.fileServer.zeroc._FileServicePrxI;
import com.maoding.storage.zeroc.NodeFileDTO;
import com.maoding.storage.zeroc.StorageServicePrx;
import com.maoding.user.zeroc.AccountDTO;

/**
 * 深圳市卯丁技术有限公司
 * @author  : 张成亮
 * 日    期 : 2018/1/8 18:43
 * 描    述 :
 */
public class FileServicePrxImpl extends CoreRemoteService<FileServicePrx> implements FileServicePrx{
    private static FileServicePrx lastPrx = null;
    private static String lastConfig = null;
    private static FileService localService = null;


    private FileService getLocalService(){
        if (localService == null) {
            localService = SpringUtils.getBean(FileService.class);
        }
        return localService;
    }

    public static FileServicePrx getInstance(String config, StorageServicePrx storageServicePrx) throws CustomException {
        if ((lastPrx == null) || (StringUtils.isNotSame(lastConfig,config))){
            FileServicePrxImpl prx = new FileServicePrxImpl();
            lastPrx = prx.getServicePrx(config, FileServicePrx.class,  _FileServicePrxI.class, prx);
            lastConfig = config;
            if (storageServicePrx != null) {
                lastPrx.setStorageService(storageServicePrx);
            }
        }
        return lastPrx;
    }

    @Override
    public void setStorageService(StorageServicePrx storageService) throws CustomException {
        getLocalService().setStorageService(storageService,null);
    }

    @Override
    public long getFileLength(NodeFileDTO file, boolean readOnly) throws CustomException {
        return getLocalService().getFileLength(file,readOnly,null);
    }

    @Override
    public FileDataDTO readFile(AccountDTO account, NodeFileDTO file, long pos, int size) throws CustomException {
        return getLocalService().readFile(account,file,pos,size,null);
    }

    @Override
    public void clearAll(AccountDTO account) throws CustomException {
        getLocalService().clearAll(account,null);
    }

    @Override
    public void flushBuffer() throws CustomException {
        getLocalService().flushBuffer(null);
    }
}
