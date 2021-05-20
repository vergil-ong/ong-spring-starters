package com.github.ong.log.util;

import com.github.ong.log.constant.LogConst;
import com.github.ong.util.RandomUtil;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 20:14
 * @Description 日志工具
 */
public class LogUtil {

    private static boolean checkLogKey(String key){
        String logVal = MDC.get(key);
        if (Objects.isNull(logVal)){
            return false;
        }

        return true;
    }

    private static String getLogVal(String logKey){
        return MDC.get(logKey);
    }

    public static void replaceTraceId(String traceId){
        MDC.put(LogConst.LOG_KEY_TRACE_ID, traceId);
    }

    public static String setAndGetTraceId(){
        String logVal = getLogVal(LogConst.LOG_KEY_TRACE_ID);
        if (Objects.nonNull(logVal)){
            return logVal;
        }

        String uuid = RandomUtil.getUUID();
        MDC.put(LogConst.LOG_KEY_TRACE_ID, uuid);

        return uuid;
    }
}
