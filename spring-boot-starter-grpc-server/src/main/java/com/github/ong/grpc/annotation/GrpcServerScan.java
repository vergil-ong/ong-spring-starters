package com.github.ong.grpc.annotation;

import com.github.ong.grpc.register.GrpcProviderRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 15:05
 * @Description 扫描 {@link com.github.ong.grpc.annotation.GrpcProvider} 注解的类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GrpcProviderRegister.class)
public @interface GrpcServerScan {

    /**
     * @return 包路径
     */
    String[] basePackages() default {};
}
