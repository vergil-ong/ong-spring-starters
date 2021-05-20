package com.github.ong.grpc.exception;

import java.text.MessageFormat;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/14 10:05
 * @Description grpc 方法匹配异常
 */
public class GrpcMethodMatchException extends RuntimeException{

    private static final String PATTERN = "Grpc method match Exception class is [{0}], methodName is [{1}], {2}";

    private static final String DEFAULT_ERROR_MESSAGE = "Method have to use two params. First is [Request Bean], second is [StreamObserver].";

    public GrpcMethodMatchException(String className, String methodName, String message) {
        super(MessageFormat.format(PATTERN, className, methodName, message));
    }

    public GrpcMethodMatchException(String className, String methodName) {
        this(className, methodName, DEFAULT_ERROR_MESSAGE);
    }
}
