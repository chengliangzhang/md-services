#pragma once
#include <data/CompanyData.ice>

[["java:package:com.maoding.company"]]
module zeroc {
    interface CompanyService {
        CompanyList listCompany(QueryCompanyDTO query) throws CustomException; //获取指定用户所属组织
        CompanyList listCompanyByUserId(string userId) throws CustomException; //获取指定用户所属组织
        ["deprecate"] CompanyList listCompanyForCurrent(); //获取当前账号所属组织
    };
};