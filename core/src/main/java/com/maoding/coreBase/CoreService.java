package com.maoding.coreBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/5/18 10:56
 * 描    述 :
 */
@Transactional(rollbackFor = Exception.class)
public class CoreService {
    /** 日志对象，不能用于static方法 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
}
