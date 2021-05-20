package com.github.ong.grpc.runner;

import com.github.ong.grpc.bind.GrpcProviderBindService;
import com.github.ong.grpc.config.grpc.GrpcProperties;
import com.github.ong.grpc.config.grpc.GrpcProviderServerConfig;
import com.github.ong.grpc.interceptor.server.ProviderServerInterceptor;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 16:51
 * @Description grpc 服务启动器
 */
@Slf4j
public class GrpcServiceApplicationRunner implements ApplicationRunner, DisposableBean {

    private GrpcProviderServerConfig grpcProviderServerConfig;

    private List<Server> serverList = new ArrayList<>();

    public GrpcServiceApplicationRunner(GrpcProviderServerConfig grpcProviderServerConfig) {
        this.grpcProviderServerConfig = grpcProviderServerConfig;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcProviderServerConfig.refresh();

        ConcurrentHashMap<String, List<ProviderServerInterceptor>> serverInterceptorMap = grpcProviderServerConfig.getServerInterceptorMap();

        if (MapUtils.isEmpty(serverInterceptorMap)){
            log.info("serverInterceptorMap is empty");
            return;
        }

        Map<String, ServerBuilder<?>> serverBuilderMap = new HashMap<>(serverInterceptorMap.keySet().size());

        serverInterceptorMap.forEach((serverKey, providerServerInterceptors) -> {

            log.info("server key {}", serverKey);

            GrpcProperties grpcProperties = grpcProviderServerConfig.getGrpcProperties(serverKey);

            ServerBuilder<?> serverBuilder = ServerBuilder.forPort(grpcProperties.getPort());

            log.info("providerServerInterceptors {}", providerServerInterceptors);
            
            if (CollectionUtils.isNotEmpty(providerServerInterceptors)){
                for (ProviderServerInterceptor interceptor : providerServerInterceptors){
                    serverBuilder.intercept(interceptor);
                }
            }

            addService(grpcProviderServerConfig.getServiceMethodDescProperty(serverKey), serverBuilder);
            serverBuilderMap.put(serverKey, serverBuilder);
        });

        serverList = startServer(serverBuilderMap);
        startAwaitTerminationThread(serverList);
    }

    private List<Server> startServer(Map<String, ServerBuilder<?>> serverBuilderMap){
        if (MapUtils.isEmpty(serverBuilderMap)){
            return Collections.emptyList();
        }

        List<Server> serverList = new ArrayList<>(serverBuilderMap.size());

        serverBuilderMap.forEach((port, serverBuilder) -> {
            try {
                Server server = serverBuilder.build();
                server.start();
                serverList.add(server);

                log.info("grpc server started {}", port);
            } catch (IOException e) {
                log.error("server start error ", e);
            }
        });

        return serverList;
    }

    private void startAwaitTerminationThread(List<Server> serverList){
        if (CollectionUtils.isEmpty(serverList)){
            return;
        }

        for (Server server : serverList){
            Thread thread = new Thread(() -> {
                try {
                    server.awaitTermination();
                } catch (InterruptedException e) {
                    log.warn(" grpc server stopped. {}", e.getMessage());
                }
            });
            thread.start();
        }
    }

    private void addService(ConcurrentHashMap<String, List<MethodDescriptorProperty>> serviceMethodDescPropertyMap,
                            ServerBuilder<?> serverBuilder){
        log.info("serviceMethodDescPropertyMap {}", serviceMethodDescPropertyMap);
        if (MapUtils.isEmpty(serviceMethodDescPropertyMap)){
            return;
        }

        serviceMethodDescPropertyMap.forEach((serviceName, methodDescriptorPropertyList)->{
            if (CollectionUtils.isEmpty(methodDescriptorPropertyList)){
                return;
            }
            serverBuilder.addService(new GrpcProviderBindService(serviceName, methodDescriptorPropertyList));
        });

    }

    @Override
    public void destroy() throws Exception {
        log.info("shutdown grpc server start");
        if (CollectionUtils.isEmpty(serverList)){
            log.info("no grpc server");
            return;
        }

        for (Server server : serverList){
            server.shutdown();
        }

        log.info("grpc server shutdown");
    }
}
