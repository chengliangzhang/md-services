#pragma once
#include <data/CommonData.ice>

[["java:package:com.maoding.company"]]
module zeroc {
    ["java:getset","clr:property"]
    struct CompanyDTO {
        string id; //唯一标识
        string token; //app使用的token标示
        string appOrgId;
        string accountId; //账号id（当前用户账号id）
        string currentCompanyId; //当前公司
        string companyName; //企业名称
        string majorType; //专业类别
        string certificate; //技术资质
        string mainField; //擅长领域
        string isAuthentication; //是否认证(0.否，1.是，2申请认证)
        string operatorName; //经办人
        string rejectReason; //认证不通过原因
        string companyType; //公司类型(0=小B，1＝超级大B,2=大B分支机构)
        string companyEmail; //企业邮箱
        string companyShortName; //企业简称
        string companyFax; //企业传真
        string serverType; //服务类型
        string province; //企业所属省
        string city; //企业所属市
        string county; //县或区或镇
        string legalRepresentative; //法人代表
        string companyPhone; //联系电话
        string companyAddress; //企业地址
        string status; //企业状态（生效0，1不生效）
        int groupIndex; //团队排序
        string businessLicenseNumber; //工商营业执照号码
        string organizationCodeNumber; //组织机构代码证号码
        string microUrl; //微官网地址
        string microTemplate; //微官网模板
        string groupId; //企业群ID
        string companyComment; //企业简介
        string filePath; //公司logo地址
        string fileGroup;
        string qrcodePath; //公司邀请二维码
        string sysRole; //是否是管理员0不是1是
        string relationType; //关联类别(1.自己创建的组织；0.我申请，2.受邀请)
        string attachId; //附件Idapp端使用
        string adminPassword; //管理密码 app端使用
        string orgType; //组织类型app端
        string orgPid; //组织父ID
        string roleCodes; //角色权限
        int companyStartFlag; //公司开始标识:1
        int isInCompanyFlag; //是否在公司标识:1：在，0：不在
    };
    ["java:type:java.util.ArrayList<CompanyDTO>"] sequence<CompanyDTO> CompanyList;

    ["java:getset","clr:property"]
    struct QueryCompanyDTO {
        string userIdString; //职员的用户id，可以是用","分隔的多个id
    };
};