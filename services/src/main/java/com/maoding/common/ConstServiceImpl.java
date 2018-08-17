package com.maoding.common;

import com.maoding.common.config.BufferConfig;
import com.maoding.common.dao.ConstDao;
import com.maoding.common.entity.ConstEntity;
import com.maoding.common.zeroc.*;
import com.maoding.coreBase.CoreLocalService;
import com.maoding.coreUtils.*;
import com.zeroc.Ice.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 *
 * @author : 张成亮
 * 日    期 : 2018/6/7 14:32
 * 描    述 :
 */
@Service("constService")
public class ConstServiceImpl extends CoreLocalService implements ConstService {

    @Autowired
    private BufferConfig bufferConfig;

    @Autowired
    private ConstDao constDao;

    @Override
    public String getTitle(@NotNull ConstQuery query, Current current) {
        ConstEntity ce = getConst(query,current);
        String title = null;
        if (ce != null) {
            if (StringUtils.isEmpty(query.getFieldNum())){
                title = ce.getTitle();
            } else {
                title = ce.getTitle(DigitUtils.parseInt(query.getFieldNum()));
            }
        }
        
        if (ObjectUtils.isNotEmpty(query.getConvertData())) {
            title = convertString(title,query.getConvertData());
        }
        
        return title;
    }

    @Override
    public String getExtra(ConstQuery query, Current current) {
        ConstEntity ce = getConst(query,current);
        String extra = null;
        if (ce != null) {
            if (StringUtils.isEmpty(query.getFieldNum())){
                extra = ce.getExtra();
            } else {
                extra = ce.getExtra(DigitUtils.parseInt(query.getFieldNum()));
            }
        }

        if (isValid(query.getConvertData())) {
            extra = convertString(extra,query.getConvertData());
        }
        return extra;
    }

    private boolean isValid(StringElementDTO stringElement){
        return (stringElement != null) &&
                (StringUtils.isNotEmpty(stringElement.getProjectId()));
    }

    @Override
    public List<VersionDTO>  listVersion(@NotNull VersionQuery query, Current current){
        final String suffix = ".VersionQuery";
        final long aliveTime = 1000;

        String key = JsonUtils.obj2CleanJson(query) + suffix;
        List<VersionDTO> list = bufferConfig.getListFromBuffer(VersionDTO.class,key,aliveTime);
        if (ObjectUtils.isEmpty(list)){
            list = constDao.listVersion(query);
            bufferConfig.setListToBuffer(list,key,aliveTime);
        }
        return list;
    }

    private List<ConstEntity> listConst(@NotNull ConstQuery query, Current current){
        final String suffix = ".ConstQuery";
        final long aliveTime = 10000;

        String key = JsonUtils.obj2CleanJson(query) + suffix;
        List<ConstEntity> list = bufferConfig.getListFromBuffer(ConstEntity.class,key,aliveTime);
        if (ObjectUtils.isEmpty(list)){
            list = constDao.listConst(query);
            bufferConfig.setListToBuffer(list,key,aliveTime);
        }
        return list;
    }

    private ConstEntity getConst(@NotNull ConstQuery query, Current current) {
        List<ConstEntity> list = listConst(query,current);
        return ObjectUtils.getFirst(list);
    }


    private String convertString(String s, @NotNull StringElementDTO stringElement){
        if (StringUtils.isEmpty(s)) {
            return "";
        }

        final String projectId = "{ProjectId}";
        final String projectName = "{Project}";
        final String rangeId = "{RangeId}";
        final String rangeName = "{Range}";
        final String issuePath = "{IssuePath}";
        final String taskId = "{TaskId}";
        final String taskName = "{Task}";
        final String taskPath = "{TaskPath}";
        final String designTaskPath = "{DesignTaskPath}";
        final String userId = "{UserId}";
        final String userName = "{User}";
        final String ownerUserId = "{OwnerUserId}";
        final String ownerName = "{OwnerUserName}";
        final String companyId = "{CompanyId}";
        final String companyName = "{Company}";
        final String majorName = "{Major}";
        final String versionName = "{Version}";
        final String actionName = "{Action}";
        final String skyPid = "{SkyPid}";
        final String srcPath = "{SrcPath}";
        final String srcDir = "{SrcDir}";
        final String srcName = "{SrcFile}";
        final String srcNameNoExt = "{SrcFileNoExt}";
        final String srcExt = "{Ext}";
        final String vTimeStampStart = "{Time:";
        final String vStart = "{";
        final String vEnd = "}";
        final String vSplit = ":";

        s = StringUtils.replace(s,projectId,stringElement.getProjectId());
        s = StringUtils.replace(s,projectName,stringElement.getProjectName());
        s = StringUtils.replace(s,rangeId,stringElement.getRangeId());
        s = StringUtils.replace(s,rangeName,stringElement.getRangeName());
        s = StringUtils.replace(s,issuePath,stringElement.getIssuePath());
        s = StringUtils.replace(s,taskId,stringElement.getTaskId());
        s = StringUtils.replace(s,taskName,stringElement.getTaskName());
        s = StringUtils.replace(s,taskPath,stringElement.getTaskPath());
        s = StringUtils.replace(s,designTaskPath,stringElement.getDesignTaskPath());
        s = StringUtils.replace(s,companyId,stringElement.getCompanyId());
        s = StringUtils.replace(s,companyName,stringElement.getCompanyName());
        s = StringUtils.replace(s,userId,stringElement.getUserId());
        s = StringUtils.replace(s,userName,stringElement.getUserName());
        s = StringUtils.replace(s,ownerUserId,stringElement.getOwnerUserId());
        s = StringUtils.replace(s,ownerName,stringElement.getOwnerUserName());
        s = StringUtils.replace(s,versionName,stringElement.getFileVersion());
        s = StringUtils.replace(s,majorName,stringElement.getMajorName());
        s = StringUtils.replace(s,actionName,stringElement.getActionName());
        s = StringUtils.replace(s,skyPid,stringElement.getSkyPid());
        s = StringUtils.replace(s,srcPath,stringElement.getPath());
        s = StringUtils.replace(s,srcDir,StringUtils.getDirName(stringElement.getPath()));
        s = StringUtils.replace(s,srcName,StringUtils.getFileName(stringElement.getPath()));
        s = StringUtils.replace(s,srcNameNoExt,StringUtils.getFileNameWithoutExt(stringElement.getPath()));
        s = StringUtils.replace(s,srcExt,StringUtils.getFileExt(stringElement.getPath()));

        if (s.contains(vTimeStampStart)){
            String fmt = s.substring(s.indexOf(vTimeStampStart) + vTimeStampStart.length(),s.indexOf(vEnd,s.indexOf(vTimeStampStart)));
            String timeTxt = StringUtils.getTimeStamp(StringUtils.getString(fmt,StringUtils.DEFAULT_STAMP_FORMAT));
            s = StringUtils.replace(s,vTimeStampStart + fmt + vEnd,timeTxt);
        }
        if (s.contains(vStart) && s.contains(vEnd) && (s.indexOf(vStart) < s.indexOf(vEnd))){
            //todo 替换{xxx:n}的字符串，目前只是简单替换为空，目标xxx代表classicName或classicId,n代表codeId，替换为title
            String vSpecial = s.substring(s.indexOf(vStart) + vStart.length(),s.indexOf(vEnd,s.indexOf(vStart)));
            s = StringUtils.replace(s,vStart + vSpecial + vEnd,"");
        }
        return s;
    }

}
