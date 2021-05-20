package com.github.ong.grpc.annotation;

import com.github.ong.grpc.register.GrpcServiceRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 17:17
 * @Description 扫描 {@link com.github.ong.grpc.annotation.GrpcService} 注解的类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GrpcServiceRegister.class)
public @interface GrpcClientScan {

    /**
     * @return 包路径
     */
    String[] basePackages() default {};
}
