package com.github.ong.grpc.factory;

import com.github.ong.grpc.annotation.GrpcMethod;
import com.github.ong.grpc.annotation.GrpcService;
import com.github.ong.grpc.config.grpc.GrpcPoolProperties;
import com.github.ong.grpc.config.grpc.GrpcProperties;
import com.github.ong.grpc.config.grpc.GrpcServerPropertiesHolder;
import com.github.ong.grpc.pool.GrpcClientPool;
import com.github.ong.grpc.pool.GrpcConFactory;
import com.github.ong.grpc.proxy.GrpcChannelProxy;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import com.github.ong.grpc.util.MethodDescriptorUtil;
import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 18:37
 * @Description 链接工厂
 */
@Slf4j
public class GrpcClientFactory {

    private static GrpcClientPool DEFAULT_GRPC_CLIENT_POOL = null;

    private GrpcConFactory grpcConFactory;

    private GrpcPoolProperties grpcPoolProperties;

    private GrpcServerPropertiesHolder grpcServerPropertiesHolder;

    public GrpcClientFactory(GrpcConFactory grpcConFactory,
                             GrpcPoolProperties grpcPoolProperties,
                             GrpcServerPropertiesHolder grpcServerPropertiesHolder) {
        this.grpcConFactory = grpcConFactory;
        this.grpcPoolProperties = grpcPoolProperties;
        this.grpcServerPropertiesHolder = grpcServerPropertiesHolder;
        DEFAULT_GRPC_CLIENT_POOL = new GrpcClientPool(grpcConFactory, new GrpcProperties(), grpcPoolProperties);
    }

    /**
     * key serverGroupKey
     * value grpc 连接池
     * 理论上一个 server+port 应该有相同的配置
     */
    private Map<String, GrpcClientPool> grpcClientPoolMap = new HashMap<>();

    private GrpcClientPool getGrpcProperties(String serverGroupKey,
                                             GrpcProperties grpcProperties,
                                             GrpcPoolProperties grpcPoolProperties){
        GrpcClientPool grpcClientPool = grpcClientPoolMap.get(serverGroupKey);
        if (Objects.nonNull(grpcClientPool)){
            return grpcClientPool;
        }

        if (Objects.isNull(grpcPoolProperties)){
            //如果 没有定制  则取公共连接池配置
            grpcPoolProperties = this.grpcPoolProperties;
        }

        grpcClientPool = new GrpcClientPool(grpcConFactory, grpcProperties, grpcPoolProperties);

        grpcClientPoolMap.put(serverGroupKey, grpcClientPool);

        return grpcClientPool;
    }

    public Object getClientBean(Class<?> targetClass){
        GrpcService grpcService = targetClass.getAnnotation(GrpcService.class);
        if (Objects.isNull(grpcService)){
            log.info("grpcService is null");
            return null;
        }

        GrpcProperties grpcProperties = new GrpcProperties(grpcService);
        GrpcPoolProperties grpcPoolProperties = grpcServerPropertiesHolder.getGrpcPoolProperties(grpcService);

        String serverGroupKey = GrpcProperties.getGroupKey(grpcService);
        GrpcClientPool grpcClientPool = getGrpcProperties(serverGroupKey, grpcProperties, grpcPoolProperties);
        if (Objects.isNull(grpcClientPool)){
            grpcClientPool = DEFAULT_GRPC_CLIENT_POOL;
        }

        String serviceName = grpcService.serviceName();

        GrpcChannelProxy grpcChannelProxy = new GrpcChannelProxy(grpcClientPool);

        Map<Method, GrpcMethod> methodGRpcMethodMap = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<GrpcMethod>) method -> AnnotatedElementUtils.findMergedAnnotation(method, GrpcMethod.class));

        methodGRpcMethodMap.forEach((method, grpcMethod) -> {
            MethodDescriptorProperty methodDescriptorProperty = MethodDescriptorUtil.parseMethodDescriptorProperties(method);
            methodDescriptorProperty.setGrpcServiceName(serviceName);
            methodDescriptorProperty.setGrpcFullMethodName(MethodDescriptor.generateFullMethodName(serviceName, methodDescriptorProperty.getGrpcMethodName()));
            grpcChannelProxy.addGrpcMethodProperties(methodDescriptorProperty);
        });

        return Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, grpcChannelProxy);
    }
}
