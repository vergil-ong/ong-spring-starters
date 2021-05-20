package com.github.ong.grpc.config;

import com.github.ong.grpc.annotation.GrpcServerScan;
import com.github.ong.grpc.config.grpc.GrpcProviderServerConfig;
import com.github.ong.grpc.interceptor.ServerLogInterceptor;
import com.github.ong.grpc.interceptor.server.ProviderServerInterceptor;
import com.github.ong.grpc.processor.GrpcProviderProcessor;
import com.github.ong.grpc.runner.GrpcServiceApplicationRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/17 16:57
 * @Description 服务端自动配置
 */
@Configuration
//@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "grpc.server", name = "enable", havingValue = "true", matchIfMissing = true)
@GrpcServerScan
public class GrpcServerAutoConfiguration {

    @Bean
    public ServerLogInterceptor serverLogInterceptor(){
        return new ServerLogInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcProviderServerConfig grpcProviderServerConfig(List<ProviderServerInterceptor> providerServerInterceptorList){
        return new GrpcProviderServerConfig(providerServerInterceptorList);
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcProviderProcessor grpcProviderProcessor(GrpcProviderServerConfig grpcProviderServerConfig){
        return new GrpcProviderProcessor(grpcProviderServerConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcServiceApplicationRunner grpcServiceApplicationRunner(GrpcProviderServerConfig grpcProviderServerConfig){
        return new GrpcServiceApplicationRunner(grpcProviderServerConfig);
    }
}
