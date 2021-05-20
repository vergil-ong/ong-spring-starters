package com.github.ong.asserts;


/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/14 11:11
 * @Description 断言
 */
public class Asserts {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
