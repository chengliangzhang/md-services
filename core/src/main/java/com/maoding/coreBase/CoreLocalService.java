package com.maoding.coreBase;

import com.maoding.coreUtils.SpringUtils;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/13 9:34
 * 描    述 :
 */
@Transactional(rollbackFor = Exception.class)
public class CoreLocalService implements com.zeroc.IceBox.Service, com.zeroc.Ice.Object{
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** icebox代理 */
    private ObjectAdapter adapter = null;

    @Override
    public void start(String s, Communicator communicator, String[] strings) {
        adapter = communicator.createObjectAdapter(s);
        adapter.add(SpringUtils.getBean(this.getClass()), Util.stringToIdentity(s));
        adapter.activate();
    }

    @Override
    public void stop() {
        adapter.destroy();
    }
}
