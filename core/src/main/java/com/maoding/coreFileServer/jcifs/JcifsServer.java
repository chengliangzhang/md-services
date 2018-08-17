package com.maoding.coreFileServer.jcifs;

import com.maoding.coreFileServer.BasicCallbackDTO;
import com.maoding.coreFileServer.BasicFileRequestDTO;
import com.maoding.coreFileServer.CoreFileDTO;
import com.maoding.coreFileServer.CoreFileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/30 19:09
 * 描    述 :
 */
@Service("jcifsServer")
@SuppressWarnings("deprecation")
public class JcifsServer implements CoreFileServer {
    /** 日志对象 */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取通过http方式上传文件数据库时的需要设置的部分参数
     *
     * @param src
     * @param callbackSetting
     */
    public BasicFileRequestDTO getUploadRequest(CoreFileDTO src, Integer mode, BasicCallbackDTO callbackSetting) {
        BasicFileRequestDTO result = new BasicFileRequestDTO();
        result.setUrl("http://localhost:8087/FileServer/upload");
        Map<String,String> params = new HashMap<>();
        if ((params != null) && (params.size() > 0)){
            result.getParams().putAll(params);
        }
        return result;

    }




}
