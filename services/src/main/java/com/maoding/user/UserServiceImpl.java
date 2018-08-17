package com.maoding.user;

import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreBean.CoreResponse;
import com.maoding.common.config.WebServiceConfig;
import com.maoding.common.zeroc.IdNameDTO;
import com.maoding.user.dao.RoleDao;
import com.maoding.user.dao.RoleListDao;
import com.maoding.user.zeroc.*;
import com.maoding.coreUtils.*;
import com.zeroc.Ice.Current;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/19 18:34
 * 描    述 :
 */
@Service("userService")
public class UserServiceImpl extends CoreLocalService implements UserService{

    @Autowired
    private WebServiceConfig webServiceConfig;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleListDao roleListDao;

    @Override
    public List<RoleDTO> listRole(QueryRoleDTO query, Current current) {
        return null;
    }

    @Override
    public void setWebRoleStatus(@NotNull WebRoleDTO webRole, @NotNull String statusId, Current current) {
        roleListDao.setWebRoleStatus(webRole.getWebRoleId(),statusId);
    }

    @Override
    public List<WebRoleDTO> listWebRole(@NotNull QueryWebRoleDTO query, Current current) {
        return roleDao.listWebRole(BeanUtils.cleanProperties(query));
    }

    @Override
    public List<IdNameDTO> listMember(QueryMemberDTO query, Current current) {
        return roleDao.listMember(BeanUtils.cleanProperties(query));
    }

    @Override
    public UserJoinDTO listUserJoin(Current current) {
        AccountDTO account = getCurrent(current);
        assert (account != null);
        return listUserJoinForAccount(account,current);
    }

    @Override
    public UserJoinDTO listUserJoinForAccount(@NotNull AccountDTO account, Current current) {
        UserJoinDTO userJoin = new UserJoinDTO();
        userJoin.setProjectList(roleDao.listProject(account.getId()));
        userJoin.setTaskList(roleDao.listTask(account.getId()));
        userJoin.setCompanyList(roleDao.listCompany(account.getId()));
        return userJoin;
    }

    @Override
    public List<ProjectRoleDTO> listProjectRoleByProjectId(String projectId, Current current) {
        return roleDao.listProjectRoleByProjectId(projectId);
    }

    @Override
    public boolean login(LoginDTO loginInfo, Current current) {
        long t = TraceUtils.enter(log,loginInfo);

        assert (loginInfo != null);

        if (StringUtils.isEmpty(loginInfo.getCellphone())) {
            loginInfo.setCellphone(loginInfo.getAccountId());
        }
        assert (!StringUtils.isEmpty(loginInfo.getCellphone()));
        assert (!StringUtils.isEmpty(loginInfo.getPassword()));

        CloseableHttpResponse response = HttpUtils.postData(webServiceConfig.getClient(), webServiceConfig.getLoginUrl(), webServiceConfig.getLoginParamsType(), loginInfo);
        if (!HttpUtils.isResponseOK(response)) return false;
        CoreResponse result = getResult(response);
        FileUtils.close(response);
        assert (result != null);

        TraceUtils.exit(log,t,result);
        return (result.isSuccessful());
    }

    @Override
    public AccountDTO getCurrent(Current current) {
        CloseableHttpResponse response = HttpUtils.postData(webServiceConfig.getClient(), webServiceConfig.getGetCurrentUrl());
        if (!HttpUtils.isResponseOK(response)) return null;
        CoreResponse<?> resultUnknownType = getResult(response);
        FileUtils.close(response);
        CoreResponse<Map<String,Object>> result = convertResponse(resultUnknownType);
        assert (result != null);
        Map<String,Object> data = result.getData();
        if (data == null) return null;
        AccountDTO dto = new AccountDTO();
        if (data.containsKey(webServiceConfig.getGetCurrentInfoKey())) data = convertInfoKey(data.get(webServiceConfig.getGetCurrentInfoKey()));
        if (data.containsKey(webServiceConfig.getGetCurrentIdKey())) dto.setId((String)data.get(webServiceConfig.getGetCurrentIdKey()));
        if (data.containsKey(webServiceConfig.getGetCurrentNameKey())) dto.setName((String)data.get(webServiceConfig.getGetCurrentNameKey()));
        return dto;
    }

    private Map<String,Object> convertInfoKey(Object infoKeyUnknownType){
        if (infoKeyUnknownType == null) return null;
        if (!(infoKeyUnknownType instanceof Map)) return null;
        Map<?,?> infoKeyMapUnknownType = (Map<?,?>)infoKeyUnknownType;
        Map<String,Object> infoKeyMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : infoKeyMapUnknownType.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            infoKeyMap.put(key, value);
        }
        return infoKeyMap;
    }

    private CoreResponse<Map<String,Object>> convertResponse(CoreResponse<?> resultUnknownType){
        if (resultUnknownType == null) return null;
        CoreResponse<Map<String,Object>> result = new CoreResponse<>();
        result.setCode(resultUnknownType.getCode());
        result.setMsg(resultUnknownType.getMsg());
        result.setStatus(resultUnknownType.getStatus());
        result.setInfo(resultUnknownType.getInfo());
        Map<?,?> webDataMap = HttpUtils.getResponseData(resultUnknownType,Map.class);
        if (webDataMap != null) {
            Map<String, Object> webData = new HashMap<>();
            for (Map.Entry<?, ?> entry : webDataMap.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                webData.put(key, value);
            }
            result.setData(webData);
        }
        return result;

    }



    private CoreResponse getResult(CloseableHttpResponse response){
        CoreResponse result = null;
        try {
            result = JsonUtils.json2Obj(EntityUtils.toString(response.getEntity()),CoreResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }
}
