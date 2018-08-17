package com.maoding.user.dao;

import com.maoding.common.zeroc.IdNameDTO;
import com.maoding.user.zeroc.ProjectRoleDTO;
import com.maoding.user.zeroc.QueryMemberDTO;
import com.maoding.user.zeroc.QueryWebRoleDTO;
import com.maoding.user.zeroc.WebRoleDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/16 14:17
 * 描    述 :
 */
@Repository
public interface RoleDao {
    List<WebRoleDTO> listWebRole(@NotNull QueryWebRoleDTO query);

    List<ProjectRoleDTO> listProjectRoleByProjectId(@Param("projectId")  String projectId);
    List<IdNameDTO> listProject(@Param("userId") String userId);
    List<IdNameDTO> listTask(@Param("userId") String userId);
    List<IdNameDTO> listCompany(@Param("userId") String userId);
    List<IdNameDTO> listMember(QueryMemberDTO query);
}
