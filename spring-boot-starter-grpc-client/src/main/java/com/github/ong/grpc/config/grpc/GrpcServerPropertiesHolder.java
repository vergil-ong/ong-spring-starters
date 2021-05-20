package com.github.ong.grpc.config.grpc;

import com.github.ong.grpc.annotation.GrpcService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 11:06
 * @Description grpc 配置贮存
 */
public class GrpcServerPropertiesHolder {


    private ConcurrentHashMap<String, GrpcPoolProperties> grpcServerPoolMap = new ConcurrentHashMap<>();

    public GrpcPoolProperties getGrpcPoolProperties(GrpcProperties grpcProperties){
        String groupKey = grpcProperties.getGroupKey();
        return grpcServerPoolMap.get(groupKey);
    }

    public GrpcPoolProperties getGrpcPoolProperties(GrpcService grpcService){
        String groupKey = GrpcProperties.getGroupKey(grpcService);
        return grpcServerPoolMap.get(groupKey);
    }

    public GrpcPoolProperties getGrpcPoolProperties(String serverName, Integer port){
        String groupKey = GrpcProperties.getGroupKey(serverName, port);
        return grpcServerPoolMap.get(groupKey);
    }

    public void setGrpcPoolProperties(GrpcProperties grpcProperties, GrpcPoolProperties grpcPoolProperties){
        grpcServerPoolMap.put(grpcProperties.getGroupKey(), grpcPoolProperties);
    }

    public void addGrpcPoolProperties(String serverName,
                                      Integer port,
                                      GrpcPoolProperties grpcPoolProperties){
        String groupKey = GrpcProperties.getGroupKey(serverName, port);

        grpcServerPoolMap.put(groupKey, grpcPoolProperties);
    }
}
