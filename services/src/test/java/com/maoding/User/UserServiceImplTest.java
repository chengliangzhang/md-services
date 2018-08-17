package com.maoding.User;


import com.maoding.common.servicePrx.UserServicePrxImpl;
import com.maoding.coreUtils.StringUtils;
import com.maoding.user.zeroc.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
* UserServiceImpl Tester.
*
* @author Zhangchengliang
* @since 12/06/2017
* @version 1.0
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})

public class UserServiceImplTest {
    private final String REMOTE_IP = "192.168.1.74";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private UserService localService;

    private UserServicePrx remote = null;

    private UserServicePrx getRemote(String ip){
        if (remote == null) {
            UserServicePrxImpl prx = new UserServicePrxImpl();
            remote = prx.getServicePrx("UserService@UserServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h " + ip + " -p 4061",
                    UserServicePrx.class,_UserServicePrxI.class);
        }
        return remote;
    }
    private UserServicePrx getRemote(){
        return getRemote(REMOTE_IP);
    }


    @Test
    public void loginRemote() throws Exception {
        boolean isOk;
        if (getIsLocal(REMOTE_IP)) {
            isOk = localService.login(getLoginInfo(),null);
        } else {
            isOk = getRemote().login(getLoginInfo());
        }
        assert (isOk);
    }

    private boolean getIsLocal(String ip) {
        return StringUtils.isEmpty(ip);
    }

    private LoginDTO getLoginInfo() {
        return new LoginDTO(
                "",
                "",
                false,
                "123456",
                "13680809727");
    }

    @Test
    @Ignore
    public void testSetRoleStatus() throws Exception {
        localService.setWebRoleStatus(getLocalWebRole(),"1",null);
    }

    private WebRoleDTO getLocalWebRole() throws Exception {
        QueryWebRoleDTO query = new QueryWebRoleDTO();
        query.setId("4-f56e157939754cd1aa2ea77e5f90de9a-d437448683314cad91dc30b68879901d");
        List<WebRoleDTO> list = localService.listWebRole(query,null);
        return list.get(0);
    }

}
