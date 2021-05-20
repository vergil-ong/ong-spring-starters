package com.github.ong.grpc.exception;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/19 14:17
 * @Description grpc server 创建异常
 */
public class GrpcServerCreateException extends RuntimeException{

    public GrpcServerCreateException(String message) {
        super(message);
    }
}
