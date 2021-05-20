package com.github.ong.grpc.config;


import com.github.ong.grpc.annotation.GrpcClientScan;
import com.github.ong.grpc.config.grpc.GrpcPoolProperties;
import com.github.ong.grpc.config.grpc.GrpcServerPropertiesHolder;
import com.github.ong.grpc.factory.GrpcClientFactory;
import com.github.ong.grpc.interceptor.ClientLogInterceptor;
import com.github.ong.grpc.pool.GrpcConFactory;
import io.grpc.ClientInterceptor;
import org.apache.commons.collections4.CollectionUtils;
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
 * @Description 客户端自动配置
 */
@Configuration
@EnableConfigurationProperties({GrpcPoolProperties.class})
@ConditionalOnProperty(prefix = "grpc.client", name = "enable", havingValue = "true", matchIfMissing = true)
@GrpcClientScan
public class GrpcClientAutoConfiguration {

    private GrpcPoolProperties grpcPoolProperties;

    private List<ClientInterceptor> clientInterceptorList;

    @Autowired
    public GrpcClientAutoConfiguration(GrpcPoolProperties grpcPoolProperties) {
        this.grpcPoolProperties = grpcPoolProperties;
    }

    @Autowired(required = false)
    public void setClientInterceptorList(List<ClientInterceptor> clientInterceptorList) {
        this.clientInterceptorList = clientInterceptorList;
    }

    @Bean
    public ClientLogInterceptor clientLogInterceptor(){
        ClientLogInterceptor clientLogInterceptor = new ClientLogInterceptor();
        return clientLogInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcConFactory grpcConFactory(){
        GrpcConFactory grpcConFactory = new GrpcConFactory();
        if (CollectionUtils.isNotEmpty(clientInterceptorList)){
            grpcConFactory.setClientInterceptorList(clientInterceptorList);
        }

        return grpcConFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcServerPropertiesHolder grpcServerPropertiesHolder(){
        GrpcServerPropertiesHolder grpcServerPropertiesHolder = new GrpcServerPropertiesHolder();
        //如果想给某个服务 设定特性化 连接池配置
        /*GrpcPoolProperties grpcPoolProperties = new GrpcPoolProperties();
        grpcPoolProperties.setMaxTotal(4);
        grpcServerPropertiesHolder.addGrpcPoolProperties("localhost", 8001, grpcPoolProperties);*/
        return grpcServerPropertiesHolder;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrpcClientFactory grpcClientFactory(GrpcConFactory grpcConFactory,
                                               GrpcServerPropertiesHolder grpcServerPropertiesHolder){
        GrpcClientFactory grpcClientFactory = new GrpcClientFactory(grpcConFactory, grpcPoolProperties, grpcServerPropertiesHolder);

        return grpcClientFactory;
    }


}
