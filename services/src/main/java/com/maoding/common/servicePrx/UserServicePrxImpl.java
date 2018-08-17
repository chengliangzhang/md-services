package com.maoding.common.servicePrx;

import com.maoding.common.zeroc.CustomException;
import com.maoding.common.zeroc.IdNameDTO;
import com.maoding.coreBase.CoreRemoteService;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.user.zeroc.*;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/8 11:54
 * 描    述 :
 */
public class UserServicePrxImpl extends CoreRemoteService<UserServicePrx> implements UserServicePrx {

    private static UserServicePrx lastPrx = null;
    private static String lastConfig = null;
    private static UserService localService = null;

    private UserService getLocalService(){
        if (localService == null) {
            localService = SpringUtils.getBean(UserService.class);
        }
        return localService;
    }

    public static UserServicePrx getInstance(String config) {
        if ((lastPrx == null) || (StringUtils.isNotSame(lastConfig,config))){
            UserServicePrxImpl prx = new UserServicePrxImpl();
            lastPrx = prx.getServicePrx(config, UserServicePrx.class, _UserServicePrxI.class, prx);
            lastConfig = config;
        }
        return lastPrx;
    }

    @Override
    public void setWebRoleStatus(WebRoleDTO webRole, String statusId)  throws CustomException {
        getLocalService().setWebRoleStatus(webRole,statusId,null);
    }

    @Override
    public List<WebRoleDTO> listWebRole(QueryWebRoleDTO query) throws CustomException  {
        return getLocalService().listWebRole(query,null);
    }

    @Override
    public AccountDTO getCurrent() {
        AccountDTO account = new AccountDTO();
        account.setId("07649b3d23094f28bfce78930bf4d4ac");
        account.setName("卢沂");
        return account;
    }

    @Override
    public boolean login(LoginDTO loginInfo) throws CustomException  {
        return true;
    }

    @Override
    public List<ProjectRoleDTO> listProjectRoleByProjectId(String projectId)  throws CustomException {
        return getLocalService().listProjectRoleByProjectId(projectId,null);
    }

    @Override
    public UserJoinDTO listUserJoin() throws CustomException  {
        return getLocalService().listUserJoin(null);
    }

    @Override
    public UserJoinDTO listUserJoinForAccount(AccountDTO account)  throws CustomException {
        return getLocalService().listUserJoinForAccount(account,null);
    }

    @Override
    public List<IdNameDTO> listMember(QueryMemberDTO query) throws CustomException  {
        return getLocalService().listMember(query,null);
    }
}
