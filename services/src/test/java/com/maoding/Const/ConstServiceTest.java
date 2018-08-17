package com.maoding.Const;

import com.maoding.common.constDefine.Const;
import com.maoding.common.zeroc.ConstQuery;
import com.maoding.common.zeroc.ConstService;
import com.maoding.common.zeroc.ConstServicePrx;
import com.maoding.common.zeroc._ConstServicePrxI;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
* ConstService Tester.
*
* @author Zhangchengliang
* @since 01/13/2018
* @version 1.0
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})

public class ConstServiceTest {
    private final String REMOTE_IP = "192.168.1.74";

    @Autowired
    private ConstService localService;

    private ConstServicePrx remote = null;

    private ConstServicePrx getRemote(String ip){
        if (remote == null) {
            CoreRemoteService<ConstServicePrx> prx = new CoreRemoteService<>();
            remote = prx.getServicePrx("ConstService@ConstServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h " + ip + " -p 4061",
                    ConstServicePrx.class,_ConstServicePrxI.class);
        }
        return remote;
    }
    private ConstServicePrx getRemote(){
        return getRemote(REMOTE_IP);
    }

    @Test
    public void testGetExtraByIdFromDatabase() throws Exception {
        ConstQuery query = new ConstQuery();
        query.setClassicId(Const.CLASSIC_TYPE_PNODE);
        query.setCodeId("0");

        String extra;
        extra = getExtra(getIsLocal(REMOTE_IP),query);
        assert(StringUtils.isSame(extra,"501;521"));
    }

    @Test
    public void testGetTitleByIdFromDatabase() throws Exception {
        ConstQuery query = new ConstQuery();
        query.setClassicId(Const.CLASSIC_TYPE_STORAGE_NODE);
        query.setCodeId("0");

        String title;
        title = getTitle(getIsLocal(REMOTE_IP),query);
        assert(StringUtils.isSame(title,"未知类型文件"));
    }

    private String getExtra(boolean isLocal,ConstQuery query) throws Exception {
        String extra;
        if (isLocal){
            extra = localService.getExtra(query,null);
        } else {
            extra = "";
        }
        return extra;
    }

    private String getTitle(boolean isLocal,ConstQuery query) throws Exception {
        String title;
        if (isLocal){
            title = localService.getTitle(query,null);
        } else {
            title = "";
        }
        return title;
    }

    private boolean getIsLocal(String ip) {
        return StringUtils.isEmpty(ip);
    }
}
