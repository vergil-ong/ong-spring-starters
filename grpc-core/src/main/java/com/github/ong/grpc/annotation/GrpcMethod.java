package com.github.ong.grpc.annotation;

import io.grpc.MethodDescriptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 16:50
 * @Description grpc 方法注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GrpcMethod {

    String methodName() default "";

    MethodDescriptor.MethodType methodType() default MethodDescriptor.MethodType.UNARY;
}
