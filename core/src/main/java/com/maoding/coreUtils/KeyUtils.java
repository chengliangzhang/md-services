package com.maoding.coreUtils;

import java.util.Random;

/**
 * Created by Dong_Liu
 * date：2017/11/3
 */
public class KeyUtils {
    /**
     * 生成唯一的主键
     * 格式: 时间+随机数
     * @return
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
