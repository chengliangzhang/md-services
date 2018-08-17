package com.maoding.coreBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 19:12
 * 描    述 :
 */
public class CoreController {
    /** 日志对象，不能用于static方法 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** 保存进入controller时的条件 */
    @ModelAttribute
    public void before() {
    }
}
