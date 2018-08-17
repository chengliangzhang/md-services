package com.maoding;

import com.maoding.coreBean.CoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Wuwq on 2016/12/14.
 * 全局异常统一处理
 */
@ControllerAdvice
public class ResponseForException {
    private final static Logger log = LoggerFactory.getLogger(ResponseForException.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CoreResponse getResponseByException(HttpServletRequest request, Exception e) {
        return null;
    }
}
