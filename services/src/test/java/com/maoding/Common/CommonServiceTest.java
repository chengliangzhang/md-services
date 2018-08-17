package com.maoding.Common;

import com.maoding.common.servicePrx.CommonServicePrxImpl;
import com.maoding.common.zeroc.CommonService;
import com.maoding.common.zeroc.CommonServicePrx;
import com.maoding.common.zeroc.VersionDTO;
import com.maoding.common.zeroc._CommonServicePrxI;
import com.maoding.coreUtils.StringUtils;
import com.maoding.fileServer.zeroc.FileDataDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 深圳市卯丁技术有限公司
 *
 * @author : 张成亮
 * 日    期 : 2018/6/8 11:21
 * 描    述 :
 */


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class CommonServiceTest {
    private final String REMOTE_IP = "192.168.1.74";

    @Autowired
    private CommonService localService;

    private CommonServicePrx remote = null;

    private CommonServicePrx getRemote(String ip){
        if (remote == null) {
            CommonServicePrxImpl prx = new CommonServicePrxImpl();
            remote = prx.getServicePrx("CommonService@CommonServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h " + ip + " -p 4061",
                    CommonServicePrx.class,_CommonServicePrxI.class);
        }
        return remote;
    }
    private CommonServicePrx getRemote(){
        return getRemote(REMOTE_IP);
    }

    @Test
    public void testUpdateService() throws Exception {
        if (getIsLocal(REMOTE_IP)) {
            localService.updateService(null);
        } else {
            getRemote().updateService();
        }
    }

    @Test
    public void testGetNewestClient() throws Exception {
        VersionDTO version;
        for (int i=0; i<1; i++) {
            if (getIsLocal(REMOTE_IP)) {
                version = localService.getNewestClient(null);
            } else {
                version = getRemote().getNewestClient();
            }
            assert (StringUtils.isSame(version.getVersionName(), "v2.1"));
        }
    }

    @Test
    public void testGetVersionLength() throws Exception {
        long len;
        if (getIsLocal(REMOTE_IP)){
            len = localService.getVersionLength(getVersion(true),null);
        } else {
            len = getRemote().getVersionLength(getVersion(false));
        }
        assert (len > 0);
    }

    @Test
    public void testReadVersion() throws Exception {
        FileDataDTO data;
        if (getIsLocal(REMOTE_IP)){
            data = localService.readVersion(getVersion(true),0,100,null);
        } else {
            data = getRemote().readVersion(getVersion(false),0,100);
        }
        assert (data != null) && (data.getSize() > 0);
    }

    private boolean getIsLocal(String ip) {
        return StringUtils.isEmpty(ip);
    }

    private VersionDTO getVersion(boolean isLocal) throws Exception {
        VersionDTO version;
        if (isLocal) {
            version = localService.getNewestClient(null);
        } else {
            version = getRemote().getNewestClient();
        }
        return version;
    }
}
