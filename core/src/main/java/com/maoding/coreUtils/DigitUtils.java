package com.maoding.coreUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/9 9:41
 * 描    述 :
 */
public class DigitUtils {
    private static final Logger log = LoggerFactory.getLogger(DigitUtils.class);

    private static final double LIMIT_0 = 0.0000001; //归零值

    public static Boolean isSame(final Object num1, final Object num2){
        if ((num1 == null) && (num2 == null)) return true;
        if ((num1 == null) || (num2 == null)) return false;
        String n1 = num1.toString();
        String n2 = num2.toString();
        assert (StringUtils.isNumeric(n1) && StringUtils.isNumeric(n2));
        Double d1 = Double.parseDouble(n1);
        Double d2 = Double.parseDouble(n2);
        return ((-LIMIT_0 < (d2 - d1)) && ((d2 - d1) < LIMIT_0));
    }

    public static Boolean isDigitalClass(final Class<?> clazz){
        return (clazz.isPrimitive()
                || clazz.isAssignableFrom(Boolean.class)
                || clazz.isAssignableFrom(Byte.class)
                || clazz.isAssignableFrom(Character.class)
                || clazz.isAssignableFrom(Short.class)
                || clazz.isAssignableFrom(Integer.class)
                || clazz.isAssignableFrom(Long.class)
                || clazz.isAssignableFrom(Float.class)
                || clazz.isAssignableFrom(Double.class)
        );
    }

    public static long parseLong(final Object value){
        if (ObjectUtils.isEmpty(value)) {
            return 0L;
        } else if (value.getClass().isPrimitive()) {
            if (value.getClass() == boolean.class) {
                return ((boolean)value) ? 1L : 0L;
            } else {
                return (long) value;
            }
        } else if (value instanceof Boolean) {
            return ((Boolean)value) ? 1L : 0L;
        } else {
            try {
                if ("true".equalsIgnoreCase(value.toString())) {
                    return 1L;
                } else if ("false".equalsIgnoreCase(value.toString())) {
                    return 0L;
                } else {
                    return Long.parseLong(value.toString());
                }
            } catch (NumberFormatException e) {
                log.warn("无法转换" + value.toString());
                return 0L;
            }
        }
    }

    public static boolean parseBoolean(final Object value){
        if ((value != null) && (value.getClass() == boolean.class)) {
            return (boolean)value;
        }
        return (parseLong(value) != 0L);
    }

    public static char parseChar(final Object value){
        if ((value != null) && (value.getClass() == char.class)) {
            return (char)value;
        }
        return (char)parseLong(value);
    }

    public static byte parseByte(final Object value){
        if ((value != null) && (value.getClass() == byte.class)) {
            return (byte)value;
        }
        return (byte)parseLong(value);
    }

    public static short parseShort(final Object value){
        if ((value != null) && (value.getClass() == short.class)) {
            return (short)value;
        }
        return (short)parseLong(value);
    }

    public static int parseInt(final Object value){
        if ((value != null) && (value.getClass() == int.class)) {
            return (int)value;
        }
        return (int)parseLong(value);
    }

    public static double parseDouble(final Object value){
        if (ObjectUtils.isEmpty(value)) {
            return (double)0;
        } else if (value.getClass().isPrimitive()) {
            if (value.getClass() == boolean.class) {
                return ((boolean)value) ? (double)1 : (double)0;
            } else {
                return (double) value;
            }
        } else if (value instanceof Boolean) {
            return ((Boolean)value) ? (double)1 : (double)0;
        } else {
            try {
                if ("true".equalsIgnoreCase(value.toString())) {
                    return (double)1;
                } else if ("false".equalsIgnoreCase(value.toString())) {
                    return (double)0;
                } else {
                    return Double.parseDouble(value.toString());
                }
            } catch (NumberFormatException e) {
                log.warn("无法转换" + value.toString());
                return (double)0;
            }
        }
    }

    public static float parseFloat(final Object value) {
        if ((value != null) && (value.getClass() == float.class)) {
            return (float)value;
        }
        return (float)parseDouble(value);
    }
}
