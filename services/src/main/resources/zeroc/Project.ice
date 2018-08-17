#pragma once
#include <data/ProjectData.ice>

[["java:package:com.maoding.project"]]
module zeroc {
    interface ProjectService {
        ProjectList listProject(QueryProjectDTO query) throws CustomException; //查询任务
        ProjectDTO getProjectInfoById(string id) throws CustomException;
    };
};