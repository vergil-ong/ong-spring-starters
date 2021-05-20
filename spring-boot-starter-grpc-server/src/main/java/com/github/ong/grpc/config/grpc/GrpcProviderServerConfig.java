package com.github.ong.grpc.config.grpc;

import com.github.ong.grpc.constant.GrpcConfigConst;
import com.github.ong.grpc.interceptor.server.ProviderServerInterceptor;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/19 14:55
 * @Description grpc 服务提供方配置
 */
public class GrpcProviderServerConfig {

    private List<ProviderServerInterceptor> providerServerInterceptorList;

    /**
     * key : port
     * value : interceptor
     */
    private ConcurrentHashMap<String, List<ProviderServerInterceptor>> serverInterceptorMap = new ConcurrentHashMap<>();

    /**
     * server 暂时只能是本地域名，所以port 做区分标准
     */
    private ConcurrentHashMap<String, GrpcProperties> grpcProviderPropertiesMap = new ConcurrentHashMap<>();

    /**
     * key : port
     *      key : service name
     *      value : 当前service下 method description
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<MethodDescriptorProperty>>> serverServiceMethodDescriptorPropertyMap = new ConcurrentHashMap<>();


    public GrpcProviderServerConfig(List<ProviderServerInterceptor> providerServerInterceptorList) {
        this.providerServerInterceptorList = providerServerInterceptorList;
    }

    public ConcurrentHashMap<String, GrpcProperties> getGrpcProviderPropertiesMap() {
        return grpcProviderPropertiesMap;
    }

    public void addService(int port){
        //server 暂时只能是本地域名，所以port 做区分标准
        grpcProviderPropertiesMap.put(String.valueOf(port), new GrpcProperties(GrpcConfigConst.DEFAULT_GRPC_SERVER_HOST, port));
    }

    public void refresh(){

        Map<Integer, List<ProviderServerInterceptor>> portInterceptorGroupMap = providerServerInterceptorList.stream()
                .collect(Collectors.groupingBy(ProviderServerInterceptor::getPort));

        List<ProviderServerInterceptor> commonServerInterceptorList = portInterceptorGroupMap.get(GrpcConfigConst.COMMON_SERVER_INTERCEPTOR_PORT);
        if (Objects.isNull(commonServerInterceptorList)){
            commonServerInterceptorList = Collections.emptyList();
        }
        final List<ProviderServerInterceptor> finalServerInterceptorList =  commonServerInterceptorList;

        grpcProviderPropertiesMap.forEach((port, grpcProperty) -> {
            List<ProviderServerInterceptor> providerServerInterceptors = portInterceptorGroupMap.get(port);
            if (Objects.isNull(providerServerInterceptors)){
                providerServerInterceptors = new ArrayList<>();
            }

            providerServerInterceptors.addAll(finalServerInterceptorList);

            serverInterceptorMap.put(port, providerServerInterceptors);
        });
    }

    public ConcurrentHashMap<String, List<ProviderServerInterceptor>> getServerInterceptorMap(){
        return serverInterceptorMap;
    }

    public List<ProviderServerInterceptor> getProviderServerInterceptorList(String serverKey){
        return serverInterceptorMap.get(serverKey);
    }

    public GrpcProperties getGrpcProperties(String serverKey){
        return grpcProviderPropertiesMap.get(serverKey);
    }

    public void addMethodDesc(String serverKey, String serviceName, List<MethodDescriptorProperty> methodDescriptorPropertyList){
        ConcurrentHashMap<String, List<MethodDescriptorProperty>> serviceMethodDescMap = serverServiceMethodDescriptorPropertyMap.get(serverKey);
        if (Objects.isNull(serviceMethodDescMap)){
            serviceMethodDescMap = new ConcurrentHashMap<>();
            serverServiceMethodDescriptorPropertyMap.put(serverKey, serviceMethodDescMap);
        }

        List<MethodDescriptorProperty> methodDescriptorPropertyMapList = serviceMethodDescMap.get(serviceName);
        if (Objects.isNull(methodDescriptorPropertyMapList)){
            methodDescriptorPropertyMapList = new ArrayList<>();
            serviceMethodDescMap.put(serviceName, methodDescriptorPropertyMapList);
        }

        methodDescriptorPropertyMapList.addAll(methodDescriptorPropertyList);
    }

    public ConcurrentHashMap<String, List<MethodDescriptorProperty>> getServiceMethodDescProperty(String serverKey){
        return serverServiceMethodDescriptorPropertyMap.get(serverKey);
    }
}
