package com.maoding.Bean;

import com.maoding.coreBase.CoreEntity;
import com.maoding.coreUtils.BeanUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* BeanUtils Tester.
*
* @author Zhangchengliang
* @since 10/27/2017
* @version 1.0
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class BeanUtilsTest {

    @Test
    public void testCreateMapFrom() throws Exception {
        TestClass2 src = new TestClass2();
        src.setDigital(3.2f);
        src.setObjectDigital(5.2);
        src.setString(new StringBuffer("class2"));
        src.setObject(new Child(1,"child"));
        src.setArray(new Byte[]{1,2,3,null});
        src.setEntity(new CoreEntity());
        src.setList(new ArrayList<>());
        src.getList().add(1.1f);
        Map<String,Object> des = BeanUtils.createMapFrom(src,String.class,Object.class);
        Assert.assertNotNull(des);
    }

    @Test
    public void testCreateListFrom() throws Exception {
        List<Integer> src = new ArrayList<>();
        src.add(1);
        src.add(null);
        List<Float> des = BeanUtils.createListFrom(src,Float.class);
        Assert.assertNotNull(des);
        List<Short> des2 = BeanUtils.createListFrom(des,Short.class);
        Assert.assertNotNull(des2);
    }

    @Test
    public void testCreateFromSpeed() throws Exception {
        long t = System.currentTimeMillis();
        for (int i=0; i<10000; i++) {
            createFromClass1ToClass2();
        }
        System.out.println("===>testCreateFrom:" + (System.currentTimeMillis()-t) + "ms");
        long t2 = System.currentTimeMillis();
        for (int i=0; i<10000; i++) {
            copyFields();
        }
        System.out.println("===>testcopyFields:" + (System.currentTimeMillis()-t2) + "ms");
    }

    @Test
    public void testCreateFrom() throws Exception {
        createFromFatherToChild();
        createFromChildToFather();
        createFromClass1ToClass2();
        createFromClass2ToClass1();
        createFromMapToClass1();
        createFromMapToClass2();
    }

    private void createFromMapToClass1(){
        Map<String,Object> src = new HashMap<>();
        src.put("Digital",3.5);
        src.put("ObjectDigital",5.5);
        src.put("String","class2");
        src.put("Object",new Child(2,"child"));
        src.put("Array",new Byte[]{1,2,3,null});
        src.put("Entity",new CoreEntity());
        List<Float> list = new ArrayList<>();
        list.add(1.5f);
        src.put("List",list);
        TestClass1 des = BeanUtils.createFrom(src,TestClass1.class);
        Assert.assertNotNull(des);
    }

    private void createFromMapToClass2(){
        Map<String,Object> src = new HashMap<>();
        src.put("Digital",3);
        src.put("ObjectDigital",5L);
        src.put("String","class1");
        src.put("Object",new Father(1));
        src.put("Array",new byte[]{1,2,3});
        src.put("Entity",new CoreEntity());
        List<Integer> list = new ArrayList<>();
        list.add(1);
        src.put("List",list);
        TestClass2 des = BeanUtils.createFrom(src,TestClass2.class);
        Assert.assertNotNull(des);
    }

    private void createFromClass1ToClass2(){
        TestClass1 src = new TestClass1();
        src.setDigital(3);
        src.setObjectDigital(5L);
        src.setString("class1");
        src.setObject(new Father(1));
        src.setArray(new byte[]{1,2,3});
        src.setEntity(new CoreEntity());
        src.setList(new ArrayList<>());
        src.getList().add(1);
        TestClass2 des = BeanUtils.createFrom(src,TestClass2.class);
        Assert.assertNotNull(des);
    }

    private void createFromClass2ToClass1(){
        TestClass2 src = new TestClass2();
        src.setDigital(3.2f);
        src.setObjectDigital(5.2);
        src.setString(new StringBuffer("class2"));
        src.setObject(new Child(1,"child"));
        src.setArray(new Byte[]{1,2,3,null});
        src.setEntity(new CoreEntity());
        src.setList(new ArrayList<>());
        src.getList().add(1.1f);
        TestClass1 des = BeanUtils.createFrom(src,TestClass1.class);
        Assert.assertNotNull(des);
    }

    private void createFromFatherToChild(){
        Father src = new Father(1);
        Child des = BeanUtils.createFrom(src,Child.class);
        Assert.assertEquals(src.getI(),des.getI());
    }

    private void createFromChildToFather(){
        Child src = new Child(1,"child");
        Father des = BeanUtils.createFrom(src,Father.class);
        Assert.assertEquals(src.getI(),des.getI());
    }

    private void copyFields() throws Exception {
        TestClass1 src = new TestClass1();
        src.setDigital(3);
        src.setObjectDigital(5L);
        src.setString("class1");
        src.setObject(new Father(1));
        src.setArray(new byte[]{1,2,3});
        src.setEntity(new CoreEntity());
        src.setList(new ArrayList<>());
        src.getList().add(1);

        TestClass1 dst = new TestClass1();

        BeanInfo sourceBeanInfo = Introspector.getBeanInfo(src.getClass(), Object.class);
        PropertyDescriptor[] sourceProperties = sourceBeanInfo.getPropertyDescriptors();
        BeanInfo destBeanInfo = Introspector.getBeanInfo(dst.getClass(), Object.class);
        PropertyDescriptor[] destProperties = destBeanInfo.getPropertyDescriptors();

        for (int i = 0; i < sourceProperties.length; i++) {

            for (int j = 0; j < destProperties.length; j++) {

                if (sourceProperties[i].getName().equals(destProperties[j].getName())) {
                    //调用source的getter方法和dest的setter方法
                    destProperties[j].getWriteMethod().invoke(dst, sourceProperties[i].getReadMethod().invoke(src));
                    break;
                }
            }
        }
    }
}
