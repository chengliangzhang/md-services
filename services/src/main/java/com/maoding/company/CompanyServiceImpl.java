package com.maoding.company;

import com.maoding.company.zeroc.CompanyDTO;
import com.maoding.company.zeroc.CompanyService;
import com.maoding.company.zeroc.QueryCompanyDTO;
import com.zeroc.Ice.Current;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/10 19:12
 * 描    述 :
 */
@SuppressWarnings("deprecation")
@Service("companyService")
public class CompanyServiceImpl implements CompanyService {
    @Override
    public List<CompanyDTO> listCompany(QueryCompanyDTO query, Current current) {
        return null;
    }

    @Override
    public List<CompanyDTO> listCompanyForCurrent(Current current) {
        return null;
    }

    @Override
    public List<CompanyDTO> listCompanyByUserId(String userId, Current current) {
        return null;
//        List<OrganizationEntity> entityList = organizationDao.selectAll();
//        List<CompanyDTO> list = new ArrayList<>();
//        for (OrganizationEntity entity : entityList){
//            CompanyDTO dto = new CompanyDTO();
//            BeanUtils.copyProperties(entity,dto);
//            list.add(dto);
//        }
//        return list;
    }
}
