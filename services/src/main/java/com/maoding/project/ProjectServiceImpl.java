package com.maoding.project;

import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreBean.CoreResponse;
import com.maoding.common.config.WebServiceConfig;
import com.maoding.coreUtils.BeanUtils;
import com.maoding.coreUtils.FileUtils;
import com.maoding.coreUtils.HttpUtils;
import com.maoding.project.zeroc.ProjectDTO;
import com.maoding.project.zeroc.ProjectService;
import com.maoding.project.zeroc.QueryProjectDTO;
import com.zeroc.Ice.Current;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/22 16:13
 * 描    述 :
 */
@Service("projectService")
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl extends CoreLocalService implements ProjectService{
    @Autowired
    WebServiceConfig webServiceConfig;

    @Override
    public List<ProjectDTO> listProject(QueryProjectDTO query, Current current) {
        return null;
    }

    @Override
    public ProjectDTO getProjectInfoById(String id, Current current) {
        CloseableHttpResponse response = HttpUtils.postData(webServiceConfig.getClient(), webServiceConfig.getLoadProjectDetailsUrl() + "/" + id);
        if (!HttpUtils.isResponseOK(response)) return null;
        CoreResponse result = HttpUtils.getResult(response);
        FileUtils.close(response);
        assert (result != null);
        assert (result.getData() != null);
        ProjectDTO projectInfo = BeanUtils.createFrom(result.getData(),ProjectDTO.class);
        return projectInfo;
    }

}
