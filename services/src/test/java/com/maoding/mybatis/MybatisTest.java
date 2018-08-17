package com.maoding.mybatis;

import com.maoding.coreBase.CorePageDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/** 
* com.maoding.mybatis.provider Tester. 
* 
* @author ZhangChengliang
* @since 08/16/2018 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class MybatisTest {
    @Autowired
    private TestDtoDao dtoDao;
    @Autowired
    private TestEntityDao entityDao;

    @Test
    public void testGet() throws Exception {
        TestDTO dto;
        dto = dtoDao.selectById("00059b63fad44e409982821886887077");
        assert (dto.getId() != null);
        dto = dtoDao.getFirst(getQuery());
        assert (dto.getId() != null);

        TestEntity entity;
        entity = entityDao.selectById("00059b63fad44e409982821886887077");
        assert (entity.getId() != null);
    }

    @Test
    public void testList() throws Exception {
        List<TestDTO> dtoList;
        dtoList = dtoDao.list(getQuery());
        assert (dtoList != null);

        CorePageDTO<TestDTO> dtoPage;
        dtoPage = dtoDao.listPage(getQuery());
        assert (dtoPage != null);

        List<TestEntity> entityList;
        entityList = entityDao.list(getQuery());
        assert (entityList != null);
    }

    @Test
    public void testCount() throws Exception {
        int count;
        count = dtoDao.count(getQuery());
        assert (count > 0);
    }


    private TestQueryDTO getQuery(){
        TestQueryDTO query = new TestQueryDTO();
        query.setName("1.txt");
        query.setFileName("1.txt");
        query.setPageIndex(1);
        query.setPageSize(5);
        return query;
    }
} 
