package com.github.ong.grpc;

import com.github.ong.grpc.config.grpc.GrpcPoolProperties;
import com.github.ong.grpc.config.grpc.GrpcServerPropertiesHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class GrpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }

    /**
     * 如果 想为某个服务定制 连接池参数 可以这么做
     * @return
     */
    @Bean
    public GrpcServerPropertiesHolder grpcServerPropertiesHolder(){
        GrpcServerPropertiesHolder grpcServerPropertiesHolder = new GrpcServerPropertiesHolder();
        //如果想给某个服务 设定特性化 连接池配置
        GrpcPoolProperties grpcPoolProperties = new GrpcPoolProperties();
        grpcPoolProperties.setMaxTotal(4);
        grpcServerPropertiesHolder.addGrpcPoolProperties("localhost", 8001, grpcPoolProperties);
        return grpcServerPropertiesHolder;
    }
}
