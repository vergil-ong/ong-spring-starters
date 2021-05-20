package com.github.ong.util;

import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 17:53
 * @Description 数组工具类
 */
public class ArrayUtil {

    public static <T> T getObj(T[] array, int index){
        if (Objects.isNull(array)){
            return null;
        }

        if (index > array.length - 1){
            return null;
        }

        return array[index];
    }

    public static <T> boolean isEmpty(T[] array){
        if (Objects.isNull(array) || array.length == 0){
            return true;
        }
        return false;
    }
}
