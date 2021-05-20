package com.github.ong.constant.asserts;

import java.text.MessageFormat;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 16:13
 * @Description 断言模板
 */
public class AssertMessageFormat {

    public static final String NONE_NULL_MSG_PATTERN = "{0} cannot be null";

    public static String getNotNullMsg(String objName){
        return MessageFormat.format(NONE_NULL_MSG_PATTERN, objName);
    }
}
