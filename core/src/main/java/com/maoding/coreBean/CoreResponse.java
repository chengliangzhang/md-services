package com.maoding.coreBean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 19:12
 * 描    述 : 请求操作时的返回值类型
 */
public final class CoreResponse<T> implements Serializable {
    /** 日志 */
    @JsonIgnore
    private final static Logger log = LoggerFactory.getLogger(CoreResponse.class);

    public final static Integer SUCCESS = 0; //操作成功
    public final static Integer FAILED = 1; //操作失败
    public final static Integer ERROR = -1; //发生异常
    public final static Integer DATA_ERROR = -2; //数据异常
    public final static Integer NO_PERMISSION = -3; //未授权
    public final static Integer URL_NOT_FOUND = 404; //找不到URL

    public final static Map<Integer,String> DEFAULT_MESSAGE = new HashMap<Integer,String>(){
        {
            put(SUCCESS,"操作成功");
            put(FAILED, "操作失败");
            put(ERROR,"发生异常");
            put(DATA_ERROR,"数据异常");
            put(NO_PERMISSION,"未授权");
            put(URL_NOT_FOUND,"找不到URL");
        }
    };
    
    /** 返回状态，等于0-正常，小于0-发生异常，大于0-存在警告 */
    private Integer status;
    /** 返回状态文字说明 */
    private String msg;
    /** 返回对象 */
    private T data;

    /** 用于维持兼容性的字段 */
    private String code; //同AjaxMessage.code
    private Object info; //同AjaxMessage.info

    private Object extendData;

    public Object getExtendData() {
        return extendData;
    }

    public void setExtendData(Object extendData) {
        this.extendData = extendData;
    }

    public CoreResponse(Integer status, String msg, T data) {
        this.status = (status != null) ? status : SUCCESS;
        this.msg = (msg != null) ? msg : DEFAULT_MESSAGE.get(this.status);
        this.data = data;

        //记录日志
        if (isSuccessful()){
            log.debug(this.msg);
        } else if (isError()) {
            log.error(this.msg);
            if ((this.data != null) && (this.data instanceof Exception)) {
                Exception e = (Exception)data;
                e.printStackTrace();
            }
        } else {
            log.info(this.msg);
        }
    }
    public CoreResponse(String msg, T data) {this(SUCCESS,msg,data);}
    public CoreResponse(String msg) {this(msg,null);}
    public CoreResponse(Integer code, String msg) {this(code,msg,null);}
    public CoreResponse(Integer code) {this(code,null);}
    public CoreResponse() {this(SUCCESS);}

    public static <E> CoreResponse<E> success(String msg, E data) {
        return new CoreResponse<>(SUCCESS, msg, data);
    }
    public static <E> CoreResponse<E> success(E data) {
        return success(null,data);
    }
    public static <E> CoreResponse<E> success() {
        return success(null);
    }

    public static <E> CoreResponse<E> failed(String msg, E data) {
        return new CoreResponse<>(FAILED, msg, data);
    }
    public static <E> CoreResponse<E> failed(String msg) {
        return failed(msg,null);
    }
    public static <E> CoreResponse<E> failed() {
        return failed(null);
    }

    public static <E> CoreResponse<E> error(Integer code, String msg, E data) {
        return new CoreResponse<>(code, msg, data);
    }
    public static <E> CoreResponse<E> error(Integer code, String msg) {
        return error(code,msg,null);
    }
    public static <E> CoreResponse<E> error(Integer code) {
        return error(code,null);
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return SUCCESS.equals(status);
    }

    @JsonIgnore
    public boolean isError() {
        return ((status == null) || (status < 0));
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    /** 维持兼容性 */
    public String getCode() {
        String s = code;
        if (s == null){
            s =  (status != null) ? status.toString() : SUCCESS.toString();
        }
        return s;
    }

    public CoreResponse<T> setCode(String code) {
        this.code = code;
        status = Integer.parseInt(code);
        return this;
    }

    public Object getInfo() {
        Object o = info;
        if (o == null){
            o = msg;
            if (o == null) o = (status != null) ? DEFAULT_MESSAGE.get(status) : DEFAULT_MESSAGE.get(SUCCESS);
        }
        return o;
    }

    public CoreResponse<T> setInfo(Object info) {
        this.info = info;
        if (info instanceof String){
            msg = (String) info;
        }
        return this;
    }

    public static <E> CoreResponse<E> urlNotFound(String msg, E data){return error(URL_NOT_FOUND,msg,data);}
    public static <E> CoreResponse<E> dataNotFound(String msg, E data){return error(DATA_ERROR,msg,data);}
    public static <E> CoreResponse<E> error(String msg, E data){return error(ERROR,msg,data);}
    public static CoreResponse<Object> error(Object info){return error(ERROR,info.toString(),null);}
}
