#pragma once
#include <data/CommonData.ice>

#include <CommonConst.ice>
#include <Company.ice>
#include <FileServer.ice>
#include <Notice.ice>
#include <Project.ice>
#include <Storage.ice>
#include <Task.ice>
#include <User.ice>

[["java:package:com.maoding.common"]]
module zeroc {
    interface CommonService {
        ConstService* getDefaultConstService() throws CustomException; //获取本机配置的常量服务器
        StorageService* getDefaultStorageService() throws CustomException; //获取本机配置的用户服务器
        FileService* getDefaultFileService() throws CustomException; //获取本机配置的文件服务器
        UserService* getDefaultUserService() throws CustomException; //获取本机配置的用户服务器
        NoticeService* getDefaultNoticeService() throws CustomException; //获取本机配置的通告服务器

        void updateService() throws CustomException; //升级本机服务
        VersionDTO getNewestClient() throws CustomException; //获取与此文件服务器匹配的最新客户端版本
        long getVersionLength(VersionDTO version) throws CustomException; //获取版本文件的长度
        FileDataDTO readVersion(VersionDTO version,long pos,int size) throws CustomException; //读取指定版本的升级文件
    };
};