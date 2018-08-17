package com.maoding.common;

import com.maoding.common.dao.ConstDao;
import com.maoding.common.entity.ConstEntity;
import com.maoding.common.zeroc.ConstQuery;
import com.maoding.common.zeroc.IdNameDTO;
import com.maoding.common.zeroc.StringElementDTO;
import com.maoding.coreUtils.DigitUtils;
import com.maoding.coreUtils.ObjectUtils;
import com.maoding.coreUtils.SpringUtils;
import com.maoding.coreUtils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/1/12 19:21
 * 描    述 :
 */
public class LocalConstService {
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(LocalConstService.class);

    public static final short CLASSIC_TYPE_CONST = 0;
    public static final short CLASSIC_TYPE_RIGHT = 1;
    public static final short CLASSIC_TYPE_COOPERATOR = 2;
    public static final short CLASSIC_TYPE_TASK = 3;
    public static final short CLASSIC_TYPE_FEE = 4;
    public static final short CLASSIC_TYPE_NODE = 6;
    public static final short CLASSIC_TYPE_LOG = 7;
    public static final short CLASSIC_TYPE_WORK = 8;
    public static final short CLASSIC_TYPE_INVITE = 9;
    public static final short CLASSIC_TYPE_NOTICE = 10;
    public static final short CLASSIC_TYPE_USER = 12;
    public static final short CLASSIC_TYPE_COMPANY = 13;
    public static final short CLASSIC_TYPE_STORAGE_NODE = 14;
    public static final short CLASSIC_TYPE_LOCK = 15;
    public static final short CLASSIC_TYPE_SYNC = 16;
    public static final short CLASSIC_TYPE_DELETE = 17;
    public static final short CLASSIC_TYPE_FILE_SERVER = 19;
    public static final short CLASSIC_TYPE_ACTION = 20;
    public static final short CLASSIC_TYPE_PNODE = 21;
    public static final short CLASSIC_TYPE_MAJOR = 22;
    public static final short CLASSIC_TYPE_ROLE_TASK = 23;
    public static final short CLASSIC_TYPE_STORAGE_RANGE = 24;
    public static final short CLASSIC_TYPE_ROLE = 25;
    public static final short CLASSIC_TYPE_ROLE_TYPE = 26;
    public static final short CLASSIC_TYPE_NOTICE_TYPE = 27;
    public static final short CLASSIC_TYPE_WEB_PERMISSION_GROUP = 28; //web权限组类型
    public static final short CLASSIC_TYPE_WEB_PERMISSION = 29; //web权限类型
    public static final short CLASSIC_TYPE_WEB_ROLE = 30; //web member角色类型
    public static final short CLASSIC_TYPE_ANNOTATE = 31; //校审意见类型
    public static final short CLASSIC_TYPE_ANNOTATE_STATUS = 32; //校审意见状态类型
    public static final short CLASSIC_TYPE_CONFIG = 33; //配置

    //校审意见状态类型
    public static final short ANNOTATE_STATUS_TYPE_UNKNOWN = 0; //未知状态
    public static final short ANNOTATE_STATUS_TYPE_PASS = 1; //通过
    public static final short ANNOTATE_STATUS_TYPE_REFUSE = 2; //不通过

    //校审意见类型
    public static final short ANNOTATE_TYPE_UNKNOWN = 0; //未知类型
    public static final short ANNOTATE_TYPE_CHECK = 1; //校验
    public static final short ANNOTATE_TYPE_AUDIT = 2; //审核

    //节点类型
    public static final short STORAGE_NODE_TYPE_UNKNOWN = 0;
    public static final short STORAGE_NODE_TYPE_DIR_UNKNOWN = 1;
    public static final short STORAGE_NODE_TYPE_FILE_COMMIT = 321;
    public static final short STORAGE_NODE_TYPE_FILE_COMMIT_HIS = 331;
    public static final short STORAGE_NODE_TYPE_DIR_OUTPUT_WEB = 501;
    public static final short STORAGE_NODE_TYPE_DIR_OUTPUT_WEB_ARCHIVE = 520;

    //文件类型
    public static final short STORAGE_FILE_TYPE_UNKNOWN = 0;
    public static final short STORAGE_FILE_TYPE_CAD = 1;
    public static final short STORAGE_FILE_TYPE_CONTRACT = 3;
    public static final short STORAGE_FILE_TYPE_LOGO = 4;
    public static final short STORAGE_FILE_TYPE_AUTH = 5;
    public static final short STORAGE_FILE_TYPE_APP = 6;
    public static final short STORAGE_FILE_TYPE_INVITE = 7;
    public static final short STORAGE_FILE_TYPE_LICENCE = 8;
    public static final short STORAGE_FILE_TYPE_LEGAL = 9;
    public static final short STORAGE_FILE_TYPE_EXP = 20;
    public static final short STORAGE_FILE_TYPE_NOTICE = 21;
    public static final short STORAGE_FILE_TYPE_MIRROR = 22;

    //分类常量
    public static final short STORAGE_RANGE_TYPE_UNKNOWN = 0;
    public static final short STORAGE_RANGE_TYPE_DESIGN = 1;
    public static final short STORAGE_RANGE_TYPE_CA = 2;
    public static final short STORAGE_RANGE_TYPE_COMMIT = 3;

    /** 历史动作类型 */
    public static final short STORAGE_ACTION_TYPE_UNKNOWN = 0;
    public static final short STORAGE_ACTION_TYPE_BACKUP = 1;
    public static final short STORAGE_ACTION_TYPE_CHECK = 2;
    public static final short STORAGE_ACTION_TYPE_AUDIT = 3;
    public static final short STORAGE_ACTION_TYPE_COMMIT = 4;
    public static final short STORAGE_ACTION_TYPE_ISSUE = 5;
    public static final short STORAGE_ACTION_TYPE_ASK_CA = 6;

    /** 通知类型 */
    public static final short NOTICE_TYPE_UNDEFINE = 0;
    public static final short NOTICE_TYPE_USER = 1;
    public static final short NOTICE_TYPE_TASK = 2;
    public static final short NOTICE_TYPE_PROJECT = 3;
    public static final short NOTICE_TYPE_COMPANY = 4;
    public static final short NOTICE_TYPE_COMMON = 5;

    /** 文件服务器类型 */
    public static final short FILE_SERVER_TYPE_UNKNOWN = 0;
    public static final short FILE_SERVER_TYPE_DISK = 1;
    public static final short FILE_SERVER_TYPE_WEB = 2;
    public static final short FILE_SERVER_TYPE_FASTFDS = 3;
    public static final short FILE_SERVER_TYPE_ALIYUN = 4;
    public static final short FILE_SERVER_TYPE_CIFS = 5;
    public static final short FILE_SERVER_TYPE_FTP = 6;
    public static final short FILE_SERVER_TYPE_ICE = 7;

    /** web角色类型 */
    public static final short WEB_ROLE_PROJECT_CREATOR = 0;
    public static final short WEB_ROLE_PROJECT_ISSUE = 1;
    public static final short WEB_ROLE_PROJECT_DESIGN = 2;
    public static final short WEB_ROLE_TASK_RESPONSE = 3;
    public static final short WEB_ROLE_TASK_DESIGN = 4;
    public static final short WEB_ROLE_TASK_CHECK = 5;
    public static final short WEB_ROLE_TASK_AUDIT = 6;


    public static final Integer POS_IS_DIRECTORY = 1;
    public static final Integer POS_IS_PROJECT = 2;
    public static final Integer POS_IS_ISSUE = 3;
    public static final Integer POS_IS_TASK = 4;
    public static final Integer POS_IS_DESIGN = 5;
    public static final Integer POS_IS_CA = 6;
    public static final Integer POS_IS_COMMIT = 7;
    public static final Integer POS_IS_WEB = 8;
    public static final Integer POS_IS_HISTORY = 9;

    public static final String MODE_TRUE = "1";
    public static final String MODE_FALSE = "0";
    public static final String MODE_BOTH = "0,1";

    public static final String SPLIT_EXTRA = ";";
    public static final String V_END = "}";

    private static ConstDao constDao = null;
    private static Map<Short,Map<String,ConstEntity>> constMap = null;
    public static Map<Short,Map<String,ConstEntity>> getConstMap(){
        if (constMap == null) {
            if (constDao == null) {
                constDao = SpringUtils.getBean(ConstDao.class);
            }
            assert (constDao != null);
            Map<Short,Map<String,ConstEntity>> cMap = new HashMap<>();
            List<ConstEntity> constList = constDao.listConst(new ConstQuery());
            for (ConstEntity e : constList){
                Map<String, ConstEntity> vMap = cMap.computeIfAbsent(e.getClassicId(), k -> new HashMap<>());
                vMap.put(e.getCodeId(),e);
            }
            constMap = cMap;
        }
        return constMap;
    }
    
    public static Map<String,ConstEntity> getConstMap(short classicId){
        return getConstMap().get(classicId);
    }

    public static List<IdNameDTO> listName(short classicId){
        List<IdNameDTO> list = new ArrayList<>();
        Map<String,ConstEntity> vMap = getConstMap(classicId);
        if (vMap != null) {
            for(Map.Entry<String,ConstEntity> entry : vMap.entrySet()){
                IdNameDTO dto = new IdNameDTO();
                dto.setId(entry.getKey());
                dto.setName(entry.getValue().getTitle());
                list.add(dto);
            }
        }
        return list;
    }

    public static ConstEntity getConstEntity(short classicId, String codeId){
        Map<String,ConstEntity> vMap = getConstMap(classicId);
        return (vMap == null) ? null : vMap.get(codeId);
    }

    public static String getTitle(short classicId, String codeId){
        return getTitle(classicId,codeId,-1);
    }

    public static String getTitle(short classicId, String codeId, int n){
        return getTitle(classicId,codeId,null,n);
    }

    public static String getTitle(short classicId, String codeId, StringElementDTO stringElement){
        return getTitle(classicId,codeId,null,-1);
    }

    public static String getTitle(short classicId, String codeId, StringElementDTO stringElement, int n){
        String s = null;
        ConstEntity e = getConstEntity(classicId,codeId);
        if (e != null){
            s = (n > 0) ? e.getTitle(n) : e.getTitle();
            if (stringElement != null) {
                s = convertString(s,stringElement);
            }
        }
        return s;
    }

    public static String getExtra(short classicId, String codeId){
        return getExtra(classicId,codeId,-1);
    }

    public static String getExtra(short classicId, String codeId, int n){
        return getExtra(classicId,codeId,null,n);
    }

    public static String getExtra(short classicId, String codeId, StringElementDTO stringElement){
        return getExtra(classicId,codeId,stringElement,-1);
    }

    public static String getExtra(short classicId, String codeId, StringElementDTO stringElement, int n){
        String s = null;
        ConstEntity e = getConstEntity(classicId,codeId);
        if (e != null){
            s = (n > 0) ? e.getExtra(n) : e.getExtra();
            if (stringElement != null) {
                s = convertString(s,stringElement);
            }
        }
        return s;
    }

    public static String getExtraByTitle(short classicId, String title, int n){
        String s = null;
        Map<String,ConstEntity> vMap = getConstMap(classicId);
        if (ObjectUtils.isNotEmpty(vMap)) {
            for (Map.Entry<String,ConstEntity> v : vMap.entrySet()) {
                ConstEntity e = v.getValue();
                if (StringUtils.isSame(title, e.getTitle())) {
                    s = (n > 0) ? e.getExtra(n) : e.getExtra();
                    break;
                }
            }
        }
        return s;
    }

    public static String getExtraByTitle(short classicId, String title){
        return getExtraByTitle(classicId,title,-1);
    }

    public static boolean isAttrTrue(short classicId,String codeId,int pos){
        return isAttrTrue(getExtra(classicId,codeId,1),pos);
    }

    public static boolean isAttrTrue(String attr, int pos){
        return (attr != null) && (attr.length() >= pos) && (pos > 0) && (attr.charAt(pos-1) != '0');
    }

    public static List<IdNameDTO> listMajor(){
        return listName(CLASSIC_TYPE_MAJOR);
    }

    public static List<IdNameDTO> listAction(){
        return listName(CLASSIC_TYPE_ACTION);
    }

    public static String getRangeName(String rangeId){
        return getTitle(CLASSIC_TYPE_STORAGE_RANGE,rangeId);
    }

    public static String getRangeId(String typeId){
        return getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId,5);
    }

    public static String getActionName(String actionTypeId){
        return getTitle(CLASSIC_TYPE_ACTION,actionTypeId);
    }

    public static String getTopicPrefix(String noticeTypeId) {
        String sField = getNoticeTopic(noticeTypeId);
        if (StringUtils.isEmpty(sField)) return "";
        return sField.contains("{") ? sField.substring(0,sField.indexOf("{")) : sField;
    }

    public static String getNoticeTopic(String noticeTypeId) {
        return getNoticeTopic(noticeTypeId,null);
    }

    public static String getNoticeTopic(String noticeTypeId, StringElementDTO stringElement) {
        return getExtra(CLASSIC_TYPE_NOTICE_TYPE,noticeTypeId,stringElement,1);
    }

    public static String getNoticeTitle(String noticeTypeId) {
        return getExtra(CLASSIC_TYPE_NOTICE_TYPE,noticeTypeId,2);
    }

    public static String getNoticeContent(String noticeTypeId) {
        return getNoticeContent(noticeTypeId,null);
    }

    public static String getNoticeContent(String noticeTypeId, StringElementDTO stringElement) {
        return getExtra(CLASSIC_TYPE_NOTICE_TYPE,noticeTypeId, stringElement,3);
    }

    public static String getActionNodeTypeId(String actionTypeId){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,2);
    }

    public static String getActionNodePath(String actionTypeId){
        return getActionNodePath(actionTypeId,null);
    }

    public static String getActionNodePath(String actionTypeId, StringElementDTO stringElement){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,stringElement,3);
    }

    public static String getActionFileServerTypeId(String actionTypeId){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,4);
    }

    public static String getActionFileServerAddress(String actionTypeId){
        return getActionFileServerAddress(actionTypeId,null);
    }

    public static String getActionFileServerAddress(String actionTypeId, @NotNull StringElementDTO stringElement){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,stringElement,5);
    }

    public static String getActionFileServerBaseDir(String actionTypeId){
        return getActionFileServerBaseDir(actionTypeId,null);
    }

    public static String getActionFileServerBaseDir(String actionTypeId, @NotNull StringElementDTO stringElement){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,stringElement,6);
    }

    public static String getActionNoticeTypeIdString(String actionTypeId){
        return getExtra(CLASSIC_TYPE_ACTION,actionTypeId,7);
    }


    public static boolean isBackupAction(String actionTypeId){
        return STORAGE_ACTION_TYPE_BACKUP == DigitUtils.parseShort(actionTypeId);
    }

    public static String convertString(String s, @NotNull StringElementDTO stringElement){
        if (s == null) return null;

        final String PROJECT_ID = "{ProjectId}";
        final String PROJECT_NAME = "{Project}";
        final String RANGE_ID = "{RangeId}";
        final String RANGE_NAME = "{Range}";
        final String ISSUE_PATH = "{IssuePath}";
        final String TASK_ID = "{TaskId}";
        final String TASK_NAME = "{Task}";
        final String TASK_PATH = "{TaskPath}";
        final String DESIGN_TASK_PATH = "{DesignTaskPath}";
        final String USER_ID = "{UserId}";
        final String USER_NAME = "{User}";
        final String OWNER_USER_ID = "{OwnerUserId}";
        final String OWNER_NAME = "{OwnerUserName}";
        final String COMPANY_ID = "{CompanyId}";
        final String COMPANY_NAME = "{Company}";
        final String MAJOR_NAME = "{Major}";
        final String VERSION_NAME = "{Version}";
        final String ACTION_NAME = "{Action}";
        final String SKY_PID = "{SkyPid}";
        final String SRC_PATH = "{SrcPath}";
        final String SRC_DIR = "{SrcDir}";
        final String SRC_NAME = "{SrcFile}";
        final String SRC_NAME_NO_EXT = "{SrcFileNoExt}";
        final String SRC_EXT = "{Ext}";
        final String V_TIME_STAMP_START = "{Time:";
        final String V_CLASSIC_NAME_START = "{Range";
        final String V_ACTION_NAME_START = "{Action";

        s = StringUtils.replace(s,PROJECT_ID,stringElement.getProjectId());
        s = StringUtils.replace(s,PROJECT_NAME,stringElement.getProjectName());
        s = StringUtils.replace(s,RANGE_ID,stringElement.getRangeId());
        s = StringUtils.replace(s,RANGE_NAME,stringElement.getRangeName());
        s = StringUtils.replace(s,ISSUE_PATH,stringElement.getIssuePath());
        s = StringUtils.replace(s,TASK_ID,stringElement.getTaskId());
        s = StringUtils.replace(s,TASK_NAME,stringElement.getTaskName());
        s = StringUtils.replace(s,TASK_PATH,stringElement.getTaskPath());
        s = StringUtils.replace(s,DESIGN_TASK_PATH,stringElement.getDesignTaskPath());
        s = StringUtils.replace(s,COMPANY_ID,stringElement.getCompanyId());
        s = StringUtils.replace(s,COMPANY_NAME,stringElement.getCompanyName());
        s = StringUtils.replace(s,USER_ID,stringElement.getUserId());
        s = StringUtils.replace(s,USER_NAME,stringElement.getUserName());
        s = StringUtils.replace(s,OWNER_USER_ID,stringElement.getOwnerUserId());
        s = StringUtils.replace(s,OWNER_NAME,stringElement.getOwnerUserName());
        s = StringUtils.replace(s,VERSION_NAME,stringElement.getFileVersion());
        s = StringUtils.replace(s,MAJOR_NAME,stringElement.getMajorName());
        s = StringUtils.replace(s,ACTION_NAME,stringElement.getActionName());
        s = StringUtils.replace(s,SKY_PID,stringElement.getSkyPid());
        s = StringUtils.replace(s,SRC_PATH,stringElement.getPath());
        s = StringUtils.replace(s,SRC_DIR,StringUtils.getDirName(stringElement.getPath()));
        s = StringUtils.replace(s,SRC_NAME,StringUtils.getFileName(stringElement.getPath()));
        s = StringUtils.replace(s,SRC_NAME_NO_EXT,StringUtils.getFileNameWithoutExt(stringElement.getPath()));
        s = StringUtils.replace(s,SRC_EXT,StringUtils.getFileExt(stringElement.getPath()));

        if (s.contains(V_TIME_STAMP_START)){
            String fmt = s.substring(s.indexOf(V_TIME_STAMP_START) + V_TIME_STAMP_START.length(),s.indexOf(V_END,s.indexOf(V_TIME_STAMP_START)));
            String timeTxt = StringUtils.getTimeStamp(StringUtils.getString(fmt,StringUtils.DEFAULT_STAMP_FORMAT));
            s = StringUtils.replace(s,V_TIME_STAMP_START + fmt + V_END,timeTxt);
        }
        if (s.contains(V_CLASSIC_NAME_START)){
            String vClassic = s.substring(s.indexOf(V_CLASSIC_NAME_START) + V_CLASSIC_NAME_START.length(),s.indexOf(V_END,s.indexOf(V_CLASSIC_NAME_START)));
            s = StringUtils.replace(s,V_CLASSIC_NAME_START + vClassic + V_END,getRangeName(vClassic));
        }
        if (s.contains(V_ACTION_NAME_START)){
            String vAction = s.substring(s.indexOf(V_ACTION_NAME_START) + V_ACTION_NAME_START.length(),s.indexOf(V_END,s.indexOf(V_CLASSIC_NAME_START)));
            s = StringUtils.replace(s,V_ACTION_NAME_START + vAction + V_END,getActionName(vAction));
        }
        return s;
    }

    public static boolean isUnknownDirectoryType(String typeId){
        return (isCustomType(typeId)) || (STORAGE_NODE_TYPE_DIR_UNKNOWN == DigitUtils.parseShort(typeId));
    }

    public static boolean isUnknownFileType(String typeId){
        return (isCustomType(typeId)) || (STORAGE_NODE_TYPE_UNKNOWN == DigitUtils.parseShort(typeId));
    }

    public static boolean isCustomType(String typeId){
        final Integer CUSTOM_TYPE_LENGTH = 32;
        return (typeId != null) && (typeId.length() >= CUSTOM_TYPE_LENGTH);
    }


    public static boolean isDirectoryType(String typeId) {
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_DIRECTORY);
    }

    public static boolean isProjectType(String typeId) {
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_PROJECT);
    }

    public static boolean isTaskType(String typeId) {
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_TASK);
    }

    public static boolean isDesignType(String typeId) {
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_DESIGN);
    }

    public static boolean isCommitType(String typeId){
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_COMMIT);
    }

    public static boolean isHistoryType(String typeId){
        return isAttrTrue(getExtra(CLASSIC_TYPE_STORAGE_NODE,typeId),POS_IS_HISTORY);
    }

    public static boolean isSystemType(String typeId){
        return isProjectType(typeId) || isTaskType(typeId);
    }

    public static String getTypeName(String typeId){
        try {
            return getTitle(CLASSIC_TYPE_STORAGE_NODE, typeId);
        } catch (NumberFormatException e) {
            log.warn("节点类型存在问题");
            return getTitle(CLASSIC_TYPE_STORAGE_NODE, Short.toString(STORAGE_NODE_TYPE_UNKNOWN));
        }
    }

    public static String getPathType(String typeId){
        return getExtra(CLASSIC_TYPE_STORAGE_NODE, typeId, 2);
    }

    public static String getFileType(String typeId){
        return getExtra(CLASSIC_TYPE_STORAGE_NODE, typeId, 3);
    }
}
