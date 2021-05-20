package com.github.ong.log.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 16:12
 * @Description 日志输出工具类
 */
@Setter
@Getter
public class LogLayoutUtil {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static final String DEFAULT_NULL = "";

    public static final Integer MAX_LOG_SIZE = 5000;

    public static String logCollection(Collection<?> collection){
        if (Objects.isNull(collection)){
            return DEFAULT_NULL;
        }

        return String.valueOf(collection.size());
    }

    public static String logMap(Map<?,?> map){
        if (Objects.isNull(map)){
            return DEFAULT_NULL;
        }

        return String.valueOf(map.size());
    }

    private static String getObjectJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e){
            return DEFAULT_NULL;
        }
    }

    private static String logStr(String logStr, Integer logLength){
        if (Objects.isNull(logStr)){
            return null;
        }
        if (logStr.length() > logLength){
            return logStr.substring(0, logLength);
        }

        return logStr;
    }

    public static String logObject(Object object){
        if (Objects.isNull(object)){
            return DEFAULT_NULL;
        }

        if (object instanceof String){
            return logStr((String) object, MAX_LOG_SIZE);
        }

        if (object instanceof Collection){
            return logCollection((Collection<?>) object);
        }

        if (object instanceof Map){
            return logMap((Map<?, ?>) object);
        }

        String objectJson = getObjectJson(object);
        return logStr(objectJson, MAX_LOG_SIZE);
    }

}
