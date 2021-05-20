package com.github.ong.grpc.annotation;

import com.github.ong.grpc.constant.GrpcConfigConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 15:35
 * @Description grpc 服务生命注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GrpcService {

    /**
     * grpc 服务名
     * @return
     */
    String serviceName();

    /**
     * grpc服务器名
     * @return
     */
    String serverName();

    /**
     * grpc 服务端口
     * @see com.github.ong.grpc.constant.GrpcConfigConst#DEFAULT_GRPC_SERVER_PORT
     * @return
     */
    int serverPort() default 8000;
}
