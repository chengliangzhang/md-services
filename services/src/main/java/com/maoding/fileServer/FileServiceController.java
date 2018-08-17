package com.maoding.fileServer;

import com.maoding.coreBase.CoreController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/30 20:22
 * 描    述 :
 */
@RestController
@RequestMapping("/FileServer")
public class FileServiceController extends CoreController {
//    @RequestMapping(value = "/setFileServerType", method = RequestMethod.POST)
//    public ApiResponse setFileServerType(@RequestParam Integer type) {
//        FileServicePrx prx = FileServiceImpl.getInstance();
//        assert prx != null;
//        assert type != null;
//        prx.setFileServerType(type);
//        return ApiResponse.success();
//    }
//
//    @RequestMapping(value = "/getFileServerType", method = RequestMethod.POST)
//    public ApiResponse setFileServerType() {
//        FileServicePrx prx = FileServiceImpl.getInstance();
//        assert prx != null;
//        Integer type = prx.getFileServerType();
//        return ApiResponse.success(type);
//    }
//
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(HttpServletRequest request) {
        assert (request != null);
    }

    @RequestMapping(value = "/testUpload", method = RequestMethod.GET)
    public void testUpload() {
    }

//    @RequestMapping(value = "/download", method = RequestMethod.POST)
//    public DownloadResultDTO download(@RequestBody DownloadRequestDTO request) {
//        FileServicePrx prx = FileServiceImpl.getInstance();
//        assert prx != null;
//        return prx.download(request);
//    }
}
