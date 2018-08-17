#pragma once
#include <data/CommonData.ice>
#include <data/StorageData.ice>
#include <data/ProjectData.ice>

[["java:package:com.maoding.fileServer"]]
module zeroc {
    ["java:type:java.util.ArrayList<String>","deprecate"] sequence<string> KeyList;
    ["java:type:java.util.ArrayList<String>","deprecate"] sequence<string> ScopeList;

    ["java:getset","clr:property"]
    struct CommitRequestDTO {
        string actionTypeId; //提交操作id

        string pid; //提交到的skydrive上的父节点id
        ["deprecate"] string path; //要产生的文件路径

        ["deprecate"] string userId; //提交目标用户id
        string ownerUserId; //提交到的目标用户id
        string fileVersion; //提交的版本号
        string majorId; //提交的专业编号
        string majorName; //提交的专业名称
        string remark; //版本提交说明

        string serverTypeId; //提交到的文件服务器类型
        string serverAddress; //提交到的文件服务器地址
        string baseDir; //提交到的版本存放的服务器空间

        //文件校审信息
        bool isPassDesign; //已提交校审
        bool isPassCheck; //通过校验
        bool isPassAudit; //通过审核
    };

    ["java:getset","clr:property"]
    struct CreateNodeRequestDTO { //创建节点时的参数
        bool isDirectory; //是否创建目录
        string fullName; //要创建的节点名，可包含相对于父节点的路径
        long fileLength; //目标文件大小
    };

    ["java:getset","clr:property"]
    struct CommitFailDTO { //批量提交失败返回
        string id; //提交失败的节点id
    };
    ["java:type:java.util.ArrayList<CommitFailDTO>"] sequence<CommitFailDTO> CommitFailList;

    ["java:getset","clr:property"]
    struct CommitListResultDTO { //批量提交结果
        SimpleNodeList successList;
        CommitFailList failList;
    };

    ["java:getset","clr:property","deprecate"]
    struct CallbackDTO {
        string url; //回调地址
        string name; //回调服务器名称
        Map params; //回调参数
    };

    ["java:getset","clr:property","deprecate"]
    struct FileDTO {
        string scope; //空间(bucket或group)
        string key; //文件id(key或path)
    };

    ["java:getset","clr:property"]
    struct MoveNodeRequestDTO { //复制节点时的参数
        string fullName; //目标相对于目标父节点的路径
    };

    ["java:getset","clr:property"]
    struct FileDataDTO {
        long pos; //数据所在起始位置，为0则为从文件头开始
        int size; //数据有效字节数，为0则所有字节都有效
        ByteArray data; //当前分片数据
    };

    ["java:getset","clr:property"]
    struct AnnotateRequestDTO { //校审意见提交申请
        string typeId; //校审意见类型
        bool isPassed; //是否通过
        string content; //校审意见正文
        ByteArray data; //意见截图数据
        NodeFileList addAccessoryList; //要添加的附件文件列表
        NodeFileList delAccessoryList; //要删除的附件文件列表
    };

    ["java:getset","clr:property"]
    struct AccessoryRequestDTO { //附件提交申请
        string path; //附件路径
        ByteArray data; //附件文件内容
    };

    ["java:getset","clr:property"]
    struct QueryFileSimpleDTO { //简单的文件查询
        //节点属性
        string ownerUserId; //节点拥有者用户id
        string notOwnerUserId; //非节点拥有者用户id

        //布尔属性
        string designMode; //是否设计文档
        string caMode; //是否校审文档
        string commitMode; //是否提资文档
        string historyMode; //是否历史文档

        //用户信息
        string accountId; //查询者用户id

        //用户角色布尔属性
        string isTaskDesigner; //用户是否设计
        string isTaskChecker; //用户是否校对
        string isTaskAuditor; //用户是否审核
    };

    ["java:getset","clr:property"]
    struct AskFileDTO { //简单的文件查询
        //节点属性
        string id; //树节点id
        string fuzzyId; //模糊匹配id字符串
        string ownerUserId; //节点拥有者用户id
        string notOwnerUserId; //非节点拥有者用户id

        //布尔属性
        string designMode; //是否设计文档
        string caMode; //是否校审文档
        string commitMode; //是否提资文档
        string historyMode; //是否历史文档

        //用户信息
        string accountId; //查询者用户id

        //用户角色布尔属性
        string isTaskDesigner; //用户是否设计
        string isTaskChecker; //用户是否校对
        string isTaskAuditor; //用户是否审核
    };

    ["java:getset","clr:property"]
    struct AskValidFileDTO { //节点查询申请，每个属性都可以是逗号分隔的多个数据
        //文件信息
        string serverTypeId; //文件存储服务器类型
        string serverAddress; //文件存储服务器地址
        string baseDir; //文件在文件存储服务器上的存储位置
        string key; //实体文件路径
    };

    ["java:getset","clr:property"]
    struct QuerySummarySimpleDTO { //汇总查询申请
        string accountId; //用户编号
        string companyId; //公司编号
    };
};