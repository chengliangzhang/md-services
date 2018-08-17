#pragma once
#include <data/CommonData.ice>
#include <data/UserData.ice>

[["java:package:com.maoding.storage"]]
module zeroc {
    ["java:getset","clr:property","deprecate"]
    struct CANodeDTO { //节点信息（目录和文件通用信息）
        string id; //节点编号（树节点编号）
        string pid; //父节点编号
        string name; //节点名称（树节点名称或文件名称）
        long createTimeStamp; //节点建立时间
        long lastModifyTimeStamp; //节点最后修改时间

        string roleName; //在此文档上担任的角色名称

        string projectId; //节点所属项目id
        string projectName; //项目名称
        string taskId; //节点所属生产任务id
        string taskName; //任务名称
        string ownerUserId; //节点所有者用户id
        string ownerName; //节点所有者名称
        string path; //节点全路径
        string fileMd5; //MD5
        long fileLength; //文件长度
        string lastFileMd5; //最后一次提交校审时的md5
        long lastFileLength; //最后一次提交校审时的文件长度
        string lastCommitFileMd5; //最后一次提交时的md5
        long lastCommitFileLength; //最后一次提交时的文件长度
        bool isPassDesign; //已经提交过校审
        bool isPassCheck; //通过校验
        bool isPassAudit; //通过审核
    };
    ["java:type:java.util.ArrayList<CANodeDTO>"] sequence<CANodeDTO> CANodeList;

    ["java:getset","clr:property"]
    struct SimpleNodeDTO { //节点信息（目录和文件通用信息）
        string id; //节点编号（树节点编号）
        string pid; //父节点编号
        string typeId; //节点类别编号
        string name; //节点名称（树节点名称或文件名称）
        long createTimeStamp; //节点建立时间
        long lastModifyTimeStamp; //节点最后修改时间

        bool isDirectory; //节点是否目录

        string projectId; //节点所属项目id
        string taskId; //节点所属生产任务id
        string ownerUserId; //节点所有者用户id
        string path; //节点全路径
        string fileMd5; //MD5
        long fileLength; //节点长度
        string ownerName; //节点所有者名称
        string projectName; //项目名称
        string taskName; //任务名称
        bool isTaskRole; //是任务内的一个角色
        bool isTaskDesigner; //是设计
        bool isTaskChecker; //是校对
        bool isTaskAuditor; //是审核

        //即时属性
        bool isReadOnly; //节点是否只读
        bool canCreateChild; //节点可以创建子节点
    };
    ["java:type:java.util.ArrayList<SimpleNodeDTO>"] sequence<SimpleNodeDTO> SimpleNodeList;

    ["java:getset","clr:property"]
    struct HistoryDTO { //历史记录信息
        string id; //历史记录编号
        string userId; //操作人员用户id
        string userName; //操作人员名字
        string roleId; //操作人员职责id
        string roleName; //操作人员职责名称
        string actionTypeId; //操作动作编号
        string actionName; //操作动作名称
        long actionTimeStamp; //操作时间
        string actionTimeText; //操作时间文字
        string remark; //操作说明

        ["deprecate"] string fileId; //操作文件节点id
    };
    ["java:type:java.util.ArrayList<HistoryDTO>"] sequence<HistoryDTO> HistoryList;

    ["java:getset","clr:property"]
    struct FileNodeDTO { //文件信息
        SimpleNodeDTO basic; //文件节点基本信息

        //文件节点特有信息
        string id; //文件编号（不包含分类编号）
        string fileVersion; //文件版本号
        string majorName; //文件所属专业名称
        long fileLength; //文件长度
        string fileMd5; //文件校验和
        long createTimeStamp; //文件建立时间
        long lastModifyTimeStamp; //文件最后修改时间

        long lastFileLength; //最后一次提交校审时的文件长度
        string lastFileMd5; //最后一次提交校审时的md5

        long lastCommitFileLength; //最后一次提资时的文件长度
        string lastCommitFileMd5; //最后一次提资时的md5

        bool isPassDesign; //文件已经提交过校审
        bool isPassCheck; //文件已经通过校验
        bool isPassAudit; //文件已经通过审核

        //任务角色信息
        RoleList roleList; //与此文档上相关的角色
    };
    ["java:type:java.util.ArrayList<FileNodeDTO>"] sequence<FileNodeDTO> FileNodeList;

    ["java:getset","clr:property"]
    struct NodeTextDTO { //节点文字描述信息
        string path; //节点全路径
        string rangeName; //节点所属分类名称
        string companyName; //节点所属组织名称
        string companyPath; //节点所属组织路径
        string projectName; //节点所属项目名称
        string issueName; //节点所属签发任务名称
        string issuePath; //节点所属签发任务路径
        string taskName; //节点所属生产任务名称
        string taskPath; //节点所属生产任务路径
        string storagePath; //节点相对路径
        string ownerName; //节点所有者名称
        string lastModifyRoleName; //节点最后操作者角色
        string serverTypeName; //文件服务器类型名称
        string fileTypeName; //文件类型文字说明
        string majorName; //文件所属专业名称
        string mainFilePath; //主文件全路径
        string companyId; //所属公司编号
    };
    ["java:type:java.util.ArrayList<NodeTextDTO>"] sequence<NodeTextDTO> NodeTextList;

    ["java:getset","clr:property"]
    struct NodeFileDTO { //节点文件信息
        //文件节点特有信息
        string id; //文件节点编号
        string fileTypeId; //文件类型
        string fileTypeName; //文件类型名称
        string fileVersion; //文件版本号
        long fileLength; //文件长度
        ["deprecate"] string fileChecksum; //文件校验和
        string fileMd5; //文件校验和
        string majorTypeId; //文件所属专业id
        string mainFileId; //主文件id
        string companyId; //文件所属组织编号

        //历史动作信息
        long lastFileLength; //最后一次提交校审时的文件长度
        string lastFileMd5; //最后一次提交校审时的md5
        long lastCommitFileLength; //最后一次提交时的文件长度
        string lastCommitFileMd5; //最后一次提交时的md5

        //文件类型布尔属性
        ["deprecate"] bool isPassCheck; //通过校验
        ["deprecate"] bool isPassAudit; //通过审核
        bool passDesign; //已经提交过校审
        bool passCheck; //通过校验
        bool passAudit; //通过审核

        //实际文件存储位置
        string serverTypeId; //文件服务器类型id
        string serverAddress; //文件服务器地址
        string baseDir; //文件在文件服务器上的存储位置

        string readOnlyKey; //只读版本在文件服务器上的存储名称
        string writableKey; //可写版本在文件服务器上的存储名称

        //主文件信息
        string mainServerTypeId; //主文件所在文件服务器类型编号
        string mainServerAddress; //主文件所在文件服务器地址
        string mainBaseDir; //主文件在文件服务器上的位置
        string mainFileTypeId; //主文件的文件类型

        ["deprecate"] string readOnlyMirrorKey; //只读版本在本地的镜像文件相对路径
        ["deprecate"] string writableMirrorKey; //可写版本在本地的镜像文件相对路径
        //某个相关联的节点信息
        string nodeId; //节点编号
        string pid; //父节点编号
        string name; //节点名称
        long createTimeStamp; //节点建立时间
        long lastModifyTimeStamp; //节点最后修改时间
        string path; //节点路径
        string roleName; //在此节点上担任的角色名称
        string projectId; //节点所属项目id
        string projectName; //项目名称
        string taskId; //节点所属生产任务id
        string taskName; //任务名称
        string ownerUserId; //节点所有者用户id
        string ownerName; //节点所有者名称
    };
    ["java:type:java.util.ArrayList<NodeFileDTO>"] sequence<NodeFileDTO> NodeFileList;

    ["java:getset","clr:property"]
    struct FullNodeDTO { //节点完整信息
        SimpleNodeDTO basic; //节点基本信息

        StringElementDTO textInfo; //文字描述信息
        NodeFileDTO fileInfo; //文件信息

        HistoryList historyList; //相关历史列表

        ["deprecate"] string issuePath; //节点所属签发任务路径
        ["deprecate"] string taskPath; //节点所属生产任务路径
    };
    ["java:type:java.util.ArrayList<FullNodeDTO>"] sequence<FullNodeDTO> FullNodeList;

    ["java:getset","clr:property"]
    struct EmbedElementDTO { //嵌入的HTML元素，如小型位图等
        //通用属性
        string id; //元素id

        //嵌入元素属性
        string title; //元素占位文字
        ByteArray dataArray; //元素内容

        //通用属性
        long createTimeStamp; //注解建立时间
        string createTimeText; //注解建立时间文字
        long lastModifyTimeStamp; //注解最后修改时间
        string lastModifyTimeText; //注解最后修改时间文字
        string lastModifyUserId; //注解最后编辑用户id
        string lastModifyRoleId; //注解最后编辑角色id
    };
    ["java:type:java.util.ArrayList<EmbedElementDTO>"] sequence<EmbedElementDTO> EmbedElementList;

    ["java:getset","clr:property"]
    struct UpdateElementDTO { //嵌入的HTML元素更新申请
        string title; //元素占位文字
        ByteArray dataArray; //元素内容

        //通用更改申请
        ["deprecate"] long lastModifyTimeStamp; //最后修改时间
        string lastModifyUserId; //最后编辑用户id
        string lastModifyRoleId; //最后编辑角色id
    };

    ["java:getset","clr:property"]
    struct AnnotateDTO { //文件注解
        //通用属性
        string id; //文件注解编号
        string name; //文件注解标题

        //通用树节点属性
        string pid; //文件注解父节点编号
        string typeId; //文件注解类型

        //文件注解属性
        string statusId; //文件注解状态
        string content; //文件注解正文内容
        string fileId; //被批注的文件编号
        string mainFileId; //原始文件编号

        //文件注解附件信息
        EmbedElementDTO element; //首个嵌入元素
        NodeFileList accessoryList; //附件列表

        //通用属性
        long createTimeStamp; //注解建立时间
        string createTimeText; //注解建立时间文字
        long lastModifyTimeStamp; //注解最后修改时间
        string lastModifyTimeText; //注解最后修改时间文字
        string lastModifyUserId; //注解最后编辑用户id
        string lastModifyRoleId; //注解最后编辑角色id
    };
    ["java:type:java.util.ArrayList<AnnotateDTO>"] sequence<AnnotateDTO> AnnotateList;

    ["java:getset","clr:property"]
    struct UpdateAnnotateDTO { //注解更新申请
        //文件注解属性
        string statusId; //文件注解状态
        string content; //文件注解正文内容
        string fileId; //被批注的文件编号
        string mainFileId; //原始文件编号
        StringList addElementIdList; //需要添加的嵌入元素类附件编号
        StringList addFileIdList; //需要添加的文件类附件编号
        StringList delAttachmentIdList; //需要删除的附件编号

        //通用更改申请
        string typeId; //修改后的节点类型
        string pid; //修改后的父节点编号
        string path; //修改后的节点路径
        string lastModifyUserId; //申请修改的用户编号
        string lastModifyRoleId; //申请修改的角色编号
    };


    ["java:getset","clr:property"]
    struct QueryAnnotateDTO { //文件注解查询申请，每个属性都可以是逗号分隔的多个数据
        string statusId; //文件注解状态
        string fileId; //被注解的文件编号
        string mainFileId; //被注解的原始文件的编号
        string anyFileId; //被注解的文件或原始文件编号

        //通用查询属性
        string id; //记录编号
        string lastModifyUserId; //最后更改者用户编号
        string lastModifyRoleId; //最后更改者职责编号
        long startTimeStamp; //起始时间
        long endTimeStamp; //终止时间
        string accountId; //查询者用户编号

        string nodeName; //记录节点名称
        string pid; //记录父节点编号
        string path; //记录路径
        string parentPath; //记录父节点路径
        string typeId; //记录节点类型

        string fuzzyPath; //模糊匹配路径
    };

    ["java:getset","clr:property"]
    struct QueryNodeDTO { //节点查询申请，每个属性都可以是逗号分隔的多个数据
        //节点属性
        string id; //树节点id
        string fuzzyId; //模糊匹配id字符串
        string name; //节点名称
        string fuzzyName; //模糊匹配名称
        string pid; //父节点id
        string typeId; //节点类型
        string notTypeId; //过滤的节点类型
        string projectId; //节点所属项目id
        string notProjectId; //排除项目编号
        string rangeId; //节点所属分类类型
        string notRangeId; //排除分类编号
        string taskId; //节点所属生产任务id
        string notTaskId; //排除的任务编号
        string ownerUserId; //节点拥有者用户id
        string notOwnerUserId; //非节点拥有者用户id
        string lastModifyRoleId; //最后更改者职责id
        string notLastModifyRoleId; //排除的最后更改者职责编号

        //布尔属性
        string nodeTypeAttr; //文件类型掩码
        string directoryMode; //是否目录
        string designMode; //是否设计文档
        string caMode; //是否校审文档
        string commitMode; //是否提资文档
        string historyMode; //是否历史文档

        //关联其他表的属性
        string companyName; //要查询的公司名称
        string fuzzyCompanyName; //模糊匹配的公司名称
        string ownerName; //要查询的所有者名称
        string fuzzyOwnerName; //模糊匹配的所有者名称
        string lastModifyUserName; //要查询的最后修改者名称
        string fuzzyLastModifyUserName; //模糊匹配的最后修改者名称

        //各子表不同组合
        string path; //树节点全路径
        string parentPath; //父路径
        string fuzzyPath; //模糊匹配路径
        string projectName; //要查询的项目名
        string fuzzyProjectName; //要模糊查询的项目名部分
        string companyId; //节点所属组织id
        string notCompanyId; //查询中要排除的公司编号

        //用户信息
        string accountId; //查询者用户id

        //用户角色布尔属性
        string roleAttr; //用户角色布尔属性掩码
        string isTaskLeader; //用户是否任务负责人
        string isTaskDesigner; //用户是否设计
        string isTaskChecker; //用户是否校对
        string isTaskAuditor; //用户是否审核

        //影响子表是否执行的条件
        string issueId; //节点所属签发任务id

        //文件信息
        string fileId; //文件编号
        string serverTypeId; //文件存储服务器类型
        string serverAddress; //文件存储服务器地址
        string baseDir; //文件在文件存储服务器上的存储位置
        string key; //实体文件路径

        //文件布尔属性
        string statusAttr; //文件校审标志掩码
        string passDesign; //已提交校审标志
        string passCheck; //已通过校验标志
        string passAudit; //已通过审核标志

        //查询布尔属性
        string isMirror; //是否镜像
        string onlyOne; //是否只返回一行

        //返回字段
        string needPath; //结果是否包含路径信息
        string needProjectName; //结果是否包含项目名信息
        string needTaskName; //结果是否包含任务名称
        string needTaskPath; //结果是否包含任务路径
        string needOwnerName; //结果是否包含创建者用户名信息
        string needCompanyId; //结果是否包含公司编号
        string needCompanyName; //结果是否包含公司名称信息
        string needIssueId; //结果是否包含签发任务编号
        string needIssuePath; //结果是否包含签发任务路径
        string needDesignTaskPath; //结果是否包含生产任务路径
        string needLastModifyUserName; //结果是否包含最后更改者用户名信息
        string needRoleInfo; //结果是否包含用户担任的角色信息
        string needFileInfo; //结果是否包含文件信息
        string needMainFileInfo; //结果是否包含主文件信息
        string needFileHistoryInfo; //结果是否包含文件历史信息
    };

    ["java:getset","clr:property"]
    struct QueryFullNodeDTO { //节点查询申请，每个属性都可以是逗号分隔的多个数据
        string statusAttr; //文件校审标志掩码
        string passDesign; //已提交校审标志
        string passCheck; //已通过校验标志
        string passAudit; //已通过审核标志
        string nodeTypeAttr; //文件类型掩码
        string isDesign; //是否设计文档
        string isCA; //是否校审文档
        string isCommit; //是否提资文档
        string isHistory; //是否历史文档
        string userId; //用户编号
        string roleAttr; //用户角色布尔属性掩码
        string isTaskLeader; //用户是否任务负责人
        string isTaskDesigner; //用户是否设计
        string isTaskChecker; //用户是否校对
        string isTaskAuditor; //用户是否审核
        string actionAttr; //历史动作布尔属性掩码
        string askCA; //获取最后提交状态时用到的提交动作是否申请校审
        string askCommit; //获取最后提交状态时用到的提交动作是否申请提资
        string key; //实体文件路径
    };

    ["java:getset","clr:property","deprecate"]
    struct QueryCANodeDTO { //节点查询申请，每个属性都可以是逗号分隔的多个数据
        string statusAttr; //文件校审标志掩码
        string passDesign; //已提交校审标志
        string passCheck; //已通过校验标志
        string passAudit; //已通过审核标志
        string nodeTypeAttr; //文件类型掩码
        string isDesign; //是否设计文档
        string isCA; //是否校审文档
        string isCommit; //是否提资文档
        string isHistory; //是否历史文档
        string userId; //用户编号
        string roleAttr; //用户角色布尔属性掩码
        string isTaskLeader; //用户是否任务负责人
        string isTaskDesigner; //用户是否设计
        string isTaskChecker; //用户是否校对
        string isTaskAuditor; //用户是否审核
        string actionAttr; //历史动作布尔属性掩码
        string askCA; //获取最后提交状态时用到的提交动作是否申请校审
        string askCommit; //获取最后提交状态时用到的提交动作是否申请提资
    };

    ["java:getset","clr:property"]
    struct QueryNodeInfoTextDTO { //文字详细信息查询申请
        bool isQueryTypeName; //查询类型名称
    };

    ["java:getset","clr:property"]
    struct QueryNodeInfoFileDTO { //文件详细信息查询申请
        string mirrorServerTypeId; //镜像服务器类型
        string mirrorServerAddress; //镜像服务器地址
        string mirrorBaseDir; //镜像根目录
    };

    ["java:getset","clr:property"]
    struct QueryNodeInfoHistoryDTO { //历史详细信息查询申请
        long historyStartTimeStamp; //历史信息的起始时间
        long historyEndTimeStamp; //历史信息的终止时间
    };

    ["java:getset","clr:property"]
    struct QueryNodeInfoDTO { //节点详细资料查询申请
        QueryNodeInfoTextDTO textQuery; //文字信息查询申请
        QueryNodeInfoFileDTO fileQuery; //文件信息查询申请
        QueryNodeInfoHistoryDTO historyQuery; //历史信息查询申请
    };

    ["java:getset","clr:property"]
    struct QueryNodeFileDTO { //文件信息查询申请
        string id; //树节点id
        string serverTypeId; //文件或镜像服务器类型
        string serverAddress; //文件或镜像服务器地址
        string baseDir; //文件或镜像根目录
        string key; //文件标识符
    };


    ["java:getset","clr:property"]
    struct UpdateHistoryDTO { //操作历史更新申请
        //操作历史信息
        string mainFileId; //源文件编号
        string fileLength; //操作时文件长度
        string fileMd5; //操作时文件MD5
        string actionTypeId; //文件操作类型
        string remark; //文件操作注解

        //通用更改申请
        string lastModifyUserId; //最后编辑用户id
        string lastModifyRoleId; //最后编辑角色id
    };

    ["java:getset","clr:property"]
    struct UpdateNodeFileDTO { //附件或文件更新申请
        //文件服务器信息
        string serverTypeId; //文件服务器类型
        string serverAddress; //文件服务器地址
        string baseDir; //文件在文件服务器上的存储位置

        //文件信息
        string fileTypeId; //目标文件类型Id
        string fileVersion; //文件版本号
        string majorTypeId; //文件所属专业编号
        string readOnlyKey; //只读版本在文件服务器上的存储名称
        string writableKey; //可写版本在文件服务器上的存储名称
        string fileLength; //只读版本文件长度
        string fileMd5; //只读版本MD5
        string mainFileId; //源文件编号
        string companyId; //文件所属组织编号
        string ownerUserId; //文件所属用户编号

        //文件布尔属性
        string status; //布尔属性值
        string passDesign; //是否已提交过校审
        string passCheck; //是否通过校对
        string passAudit; //是否通过审核

        //通用更改申请
        string lastModifyUserId; //最后编辑用户id
        string lastModifyRoleId; //最后编辑角色id

        //关联的操作历史信息
        UpdateHistoryDTO historyUpdateRequest; //相关操作历史更新申请
    };

    ["java:getset","clr:property"]
    struct UpdateNodeDTO { //节点更改申请
        //树节点信息
        string nodeName; //节点名称
        string pid; //父节点编号
        string path; //绝对或相对路径，包含文件名
        string typeId; //节点类型

        //节点特有信息
        string ownerUserId; //拥有者用户id
        string taskId; //所属任务编号
        string projectId; //所属项目编号
        string fileLength; //节点大小
        string fileMd5; //节点MD5

        //关联的文件信息
        UpdateNodeFileDTO updateFileRequest; //相关文件更新申请
        //文件服务器信息
        string serverTypeId; //文件服务器类型
        string serverAddress; //文件服务器地址
        string baseDir; //文件在文件服务器上的存储位置

        //文件信息
        string fileTypeId; //目标文件类型Id
        string fileVersion; //文件版本号
        string majorTypeId; //文件所属专业编号
        string readOnlyKey; //只读版本在文件服务器上的存储名称
        string writableKey; //可写版本在文件服务器上的存储名称
        string mainFileId; //主文件编号

        //文件布尔属性
        string statusAttr; //布尔属性值
        string passDesign; //是否已提交过校审
        string passCheck; //是否通过校对
        string passAudit; //是否通过审核

        //关联的操作历史信息
        string actionTypeId; //文件操作类型
        string remark; //文件操作注解

        //通用更改申请
        string lastModifyUserId; //操作者用户id
        string lastModifyRoleId; //操作者职责id

        //兼容属性
        ["deprecate"] string isPassDesign; //已提交过校审
        ["deprecate"] string isPassCheck; //通过校验
        ["deprecate"] string isPassAudit; //通过审核
        ["deprecate"] string mirrorTypeId; //镜像文件服务器类型
        ["deprecate"] string mirrorAddress; //镜像文件服务器地址
        ["deprecate"] string mirrorBaseDir; //文件在镜像文件服务器上的存储位置
        ["deprecate"] string readOnlyMirrorKey; //只读版本在本地的镜像
        ["deprecate"] string writableMirrorKey; //可写版本在本地的镜像
    };

    ["java:getset","clr:property"]
    struct SummaryFileServerDTO { //服务器文件汇总信息
        string serverTypeId;
        string serverAddress;
        string BaseDir;
        long usageSize;
    };
    ["java:type:java.util.ArrayList<SummaryFileServerDTO>"] sequence<SummaryFileServerDTO> SummaryFileServerList;

    ["java:getset","clr:property"]
    struct SummaryFileCompanyDTO { //文件汇总信息
        string companyId; //公司编号
        long allocSize; //公司购买的总空间
        long usageSize; //公司已使用空间
        long skyUsageSize; //skyDriver内公司所占用的空间
        SummaryFileServerList serverList; //分配给公司的文件服务器汇总信息列表
    };
    ["java:type:java.util.ArrayList<SummaryFileCompanyDTO>"] sequence<SummaryFileCompanyDTO> SummaryFileCompanyList;

    ["java:getset","clr:property"]
    struct SummaryFileDTO { //文件汇总信息
        long totalUsageSize; //用户所在所有公司占用总空间
        long totalAllocSize; //用户所在所有公司购买总空间
        SummaryFileCompanyList companyList; //分配给公司的文件服务器汇总信息列表
    };
    ["java:type:java.util.ArrayList<SummaryFileDTO>"] sequence<SummaryFileDTO> SummaryFileList;

    ["java:getset","clr:property"]
    struct QuerySummaryDTO { //汇总查询申请
        string accountId; //用户编号
        string companyId; //公司编号
        string serverTypeId; //文件服务器类型
        string notServerTypeId; //排除的文件服务器类型
        string serverAddress; //文件服务器地址
        string baseDir; //文件在文件服务器上的存储位置
    };

    ["java:getset","clr:property"]
    struct QueryHistoryDTO { //历史信息查询，每个属性都可以是逗号分隔的多个数据
        string mainFileId; //主文件编号
        string actionAttr; //历史动作布尔属性掩码
        string actionTypeId; //历史动作编码
        string notActionTypeId; //排除的历史动作编码
        string isCA; //历史动作是否申请校审
        string isCommit; //历史动作是否申请提资
    };
};