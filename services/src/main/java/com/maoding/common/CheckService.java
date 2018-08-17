package com.maoding.common;

import com.maoding.common.zeroc.CustomException;
import com.maoding.common.zeroc.ErrorCode;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2018/3/12 10:22
 * 描    述 :
 */
public class CheckService {
    public static void check(boolean condition, ErrorCode code, String message) throws CustomException {
        if (!(condition)) {
            CustomException e = new CustomException(code,message);
            StackTraceElement[] st = e.getStackTrace();
            if (st != null && st.length > 1) {
                message += " --- " + st[1].getClassName() + "." + st[1].getMethodName() +
                        "(" + st[1].getFileName() + ":" + st[1].getLineNumber() + ")";
                e = new CustomException(code,message);
            }
            throw e;
        }
    }
    public static void check(boolean condition, ErrorCode code) throws CustomException{
        check(condition,code, "系统异常");
    }
    public static void check(boolean condition, String message) throws CustomException{
        check(condition,ErrorCode.Assert,message);
    }
    public static void check(boolean condition) throws CustomException{
        check(condition,ErrorCode.Assert);
    }

}
