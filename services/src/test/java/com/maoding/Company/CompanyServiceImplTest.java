package com.maoding.Company;


import com.maoding.company.dao.CompanyDao;
import com.maoding.company.entity.CompanyEntity;
import com.maoding.company.zeroc.CompanyService;
import com.maoding.company.zeroc.CompanyServicePrx;
import com.maoding.company.zeroc._CompanyServicePrxI;
import com.maoding.coreBase.CoreRemoteService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/25 15:08
 * 描    述 :
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class CompanyServiceImplTest {
    private final String REMOTE_IP = "192.168.1.74";

    @Autowired
    private CompanyService companyService;

    private CompanyServicePrx remote = null;

    private CompanyServicePrx getRemote(String ip){
        if (remote == null) {
            CoreRemoteService<CompanyServicePrx> prx = new CoreRemoteService<>();
            remote = prx.getServicePrx("CompanyService@ConstServer",
                    "--Ice.Default.Locator=IceGrid/Locator:tcp -h " + ip + " -p 4061",
                    CompanyServicePrx.class,_CompanyServicePrxI.class);
        }
        return remote;
    }
    private CompanyServicePrx getRemote(){
        return getRemote(REMOTE_IP);
    }

    @Autowired
    private CompanyDao companyDao;

    @Test
    @Ignore
    public void testInsert() throws Exception {
        CompanyEntity entity = new CompanyEntity();
        entity.setCompanyName("测测插入");
        entity.resetId();
        entity.setCreateBy("aaaaa");
        entity.setCreateDate(new Date());
        entity.setCreateBy("bbbbbb");
        companyDao.insert(entity);
    }
}