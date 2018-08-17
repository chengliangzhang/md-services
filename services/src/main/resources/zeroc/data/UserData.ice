#pragma once
#include <data/CommonData.ice>

[["java:package:com.maoding.user"]]
module zeroc {
    ["java:getset","clr:property"]
    struct WebRoleDTO { //web角色信息
        //通用属性
        string id; //元素id

        //角色属性
        string webRoleId; //web数据库内角色编号
        string typeId; //web数据库内角色类型编号
        string typeName; //角色类型名称

        bool isTaskDesigner; //角色是设计人员
        bool isComplete; //是否已完成

        string projectId; //项目编码
        string projectName; //项目名称
        string taskId; //任务编码
        string taskName; //任务名称
        string companyId; //公司编码
        string companyName; //公司名称

        long taskStartTimeStamp; //任务启动时间
        string taskStartTimeText; //任务启动时间文字
        long taskEndTimeStamp; //任务结束时间
        string taskEndTimeText; //任务结束时间文字

        //通用属性
        long createTimeStamp; //注解建立时间
        string createTimeText; //注解建立时间文字
        long lastModifyTimeStamp; //注解最后修改时间
        string lastModifyTimeText; //注解最后修改时间文字
    };
    ["java:type:java.util.ArrayList<WebRoleDTO>"] sequence<WebRoleDTO> WebRoleList;

    ["java:getset","clr:property"]
    struct LoginDTO { //登录信息
        string accountId; //用户id
        string encryptPassword; //密码（已进行过加密算法）
        bool isRemember; //是否记住

        //保存兼容性
        string password; //密码（未加密）
        string cellphone; //登录账号
    };

    ["java:getset","clr:property"]
    struct AccountDTO {
        string id; //唯一编号
        string name; //用户名
    };

    ["java:getset","clr:property"]
    struct RoleDTO {
        string id; //唯一编号
        string name; //职责角色名
    };
    ["java:type:java.util.ArrayList<RoleDTO>"] sequence<RoleDTO> RoleList;

    ["java:getset","clr:property"]
    struct UserDTO {
        string id; //唯一编号
        string name; //用户名
    };
    ["java:type:java.util.ArrayList<UserDTO>"] sequence<UserDTO> UserList;

    ["java:getset","clr:property"]
    struct TaskRoleDTO { //角色定义
        string id; //任务id
        string name; //任务名称
        IdNameList taskRoleList; //用户在任务中承担的角色列表
    };
    ["java:type:java.util.ArrayList<TaskRoleDTO>"] sequence<TaskRoleDTO> TaskRoleList;

    ["java:getset","clr:property"]
    struct ProjectRoleDTO {
        string userId; //用户编号
        string userName; //用户名称
        IdNameList projectRoleList; //用户在项目中承担的角色列表
        TaskRoleList taskList; //用户所参与的任务
    };
    ["java:type:java.util.ArrayList<ProjectRoleDTO>"] sequence<ProjectRoleDTO> ProjectRoleList;

    ["java:getset","clr:property"]
    struct UserJoinDTO {
        IdNameList projectList; //用户参加的项目
        IdNameList taskList; //用户参加的任务
        IdNameList companyList; //用户所属的组织
    };
    ["java:type:java.util.ArrayList<UserJoinDTO>"] sequence<UserJoinDTO> UserJoinList;

    ["java:getset","clr:property"]
    struct QueryMemberDTO {
        string userTypeIdString; //参与者角色类型id，可以是用","分隔的多个id
        string projectId; //参与项目id
        string taskId; //参与任务id
        string companyId; //参与组织id
    };

    ["java:getset","clr:property"]
    struct QueryWebRoleDTO { //web角色查询条件
        string attrStr; //web角色类型布尔属性
        string typeId; //web角色类型编码
        string userId; //要查询的用户编码
        string taskId; //要查询的生产任务编码
        string roleId; //要查询的职责类型编码

        //通用查询条件
        string id; //记录id
        string lastModifyUserId; //最后更改者用户id
        string lastModifyRoleId; //最后更改者职责id
        long startTimeStamp; //起始时间
        long endTimeStamp; //终止时间
        string accountId; //查询者用户id
    };

    ["java:getset","clr:property"]
    struct QueryRoleDTO { //角色查询条件
        string attrStr; //角色类型布尔属性
        string typeId; //角色类型编码
        string notTypeId; //排除角色类型编码
        string userId; //要查询的用户编码
        string notUserId; //排除查询的用户编码
        string projectId; //要查询的生产任务编码
        string taskId; //要查询的生产任务编码
        string companyId; //要查询的公司编码

        //通用查询条件
        string id; //记录id
        string lastModifyUserId; //最后更改者用户编号
        string lastModifyRoleId; //最后更改者职责编号
        string startTimeStamp; //起始时间，字符化的长整数
        string endTimeStamp; //终止时间，字符化的长整数
        string accountId; //查询者用户编号
    };
};