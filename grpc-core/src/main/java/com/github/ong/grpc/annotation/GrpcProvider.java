package com.github.ong.grpc.annotation;

import com.github.ong.grpc.constant.GrpcConfigConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 16:25
 * @Description grpc 服务提供注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GrpcProvider {

    /**
     * grpc 服务名
     * @see com.github.ong.grpc.constant.GrpcConfigConst#DEFAULT_GRPC_SERVICE_NAME
     * @return
     */
    String serviceName() default GrpcConfigConst.DEFAULT_GRPC_SERVICE_NAME;

    /**
     * grpc 服务端口
     * @see com.github.ong.grpc.constant.GrpcConfigConst#DEFAULT_GRPC_SERVER_PORT
     * @return
     */
    int serverPort() default GrpcConfigConst.DEFAULT_GRPC_SERVER_PORT;
}
