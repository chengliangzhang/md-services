#pragma once
#include <data/CommonData.ice>

[["java:package:com.maoding.common"]]
module zeroc {
    interface ConstService {
        string getTitle(ConstQuery query) throws CustomException; //获取指定常量的标题
        string getExtra(ConstQuery query) throws CustomException; //获取指定常量的控制字符串

        VersionList listVersion(VersionQuery query) throws CustomException; //查询版本信息
    };
};