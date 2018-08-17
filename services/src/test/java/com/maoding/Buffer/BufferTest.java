package com.maoding.Buffer;

import com.maoding.coreBuffer.CoreBuffer;
import com.maoding.coreBuffer.redis.RedisBuffer;
import com.maoding.coreUtils.ObjectUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.coreUtils.ThreadUtils;
import com.maoding.storage.zeroc.SimpleNodeDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/6/6 14:45
 * 描    述 :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.maoding"})
public class BufferTest {

    private CoreBuffer getRedisBuffer() throws Exception {
        RedisBuffer buffer = new RedisBuffer();
        buffer.setAddress("192.168.1.74:6579");
        return buffer;
    }

    @Test
    public void testGetAndSetList() throws Exception {
        getRedisBuffer().setList(getSimpleNodeKey(),getSimpleNodeList(),1000);

        List<SimpleNodeDTO> list;
        list = getRedisBuffer().getList(getSimpleNodeKey(),100);
        assert (list.size() > 0);

        ThreadUtils.sleep(1500);
        list = getRedisBuffer().getList(getSimpleNodeKey(),100);
        assert (list.size() == 0);
    }

    @Test
    public void testReplace() throws Exception {
        getRedisBuffer().setList(getSimpleNodeKey(),getSimpleNodeList(),0);
        SimpleNodeDTO node = getSimpleNode();
        node.setName("2222");

        boolean found = false;
        found = getRedisBuffer().replaceInList(node,0);
        assert (found);

        List<SimpleNodeDTO> list = getRedisBuffer().getList(getSimpleNodeKey(),100);
        assert (StringUtils.isSame(ObjectUtils.getFirst(list).getName(),node.getName()));
    }

    @Test
    public void testRemove() throws Exception {
        getRedisBuffer().setList(getSimpleNodeKey(),getSimpleNodeList(),0);

        getRedisBuffer().removeListInclude(getSimpleNode());

        assert (getRedisBuffer().getList(getSimpleNodeKey(),100).size() == 0);
    }

    private List<SimpleNodeDTO> getSimpleNodeList() throws Exception {
        List<SimpleNodeDTO> list = new ArrayList<>();
        list.add(getSimpleNode());
        return list;
    }
    private SimpleNodeDTO getSimpleNode() throws Exception {
        SimpleNodeDTO node = new SimpleNodeDTO();
        node.setId("1111");
        return node;
    }
    private String getSimpleNodeKey() throws Exception {
        return "{\"id\":\"1111\"}.SimpleNodeDTO";
    }
}
