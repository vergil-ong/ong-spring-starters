package com.github.ong.grpc.processor;

import com.github.ong.grpc.annotation.GrpcMethod;
import com.github.ong.grpc.annotation.GrpcProvider;
import com.github.ong.grpc.config.grpc.GrpcProviderServerConfig;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import com.github.ong.grpc.property.GrpcServiceProperty;
import com.github.ong.grpc.util.GrpcServiceUtil;
import com.github.ong.grpc.util.MethodDescriptorUtil;
import io.grpc.MethodDescriptor;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 17:46
 * @Description grpc 服务后置处理器
 */
@Getter
public class GrpcProviderProcessor implements BeanPostProcessor {

    private GrpcProviderServerConfig grpcProviderServerConfig;

    public GrpcProviderProcessor(GrpcProviderServerConfig grpcProviderServerConfig) {
        this.grpcProviderServerConfig = grpcProviderServerConfig;
    }

    /**
     * key : service name
     * value : 当前service下 method description
     */
    private Map<String, List<MethodDescriptorProperty>> serviceMethodDescMap = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        GrpcProvider grpcProvider = bean.getClass().getAnnotation(GrpcProvider.class);
        if (Objects.isNull(grpcProvider)){
            return bean;
        }

        GrpcServiceProperty grpcServiceProperty = GrpcServiceUtil.getGrpcServiceProperty(bean.getClass());

        grpcProviderServerConfig.addService(grpcServiceProperty.getServerPort());

        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Map<Method, GrpcMethod> methodGRpcMethodMap = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<GrpcMethod>) method -> AnnotatedElementUtils.findMergedAnnotation(method, GrpcMethod.class));

        //需要用户显示指定 GrpcMethod 不想直接映射所有方法

        if (MapUtils.isEmpty(methodGRpcMethodMap)){
            return bean;
        }

        List<MethodDescriptorProperty> methodDescriptorPropertyList = new ArrayList<>();
        methodGRpcMethodMap.forEach((method, grpcMethod) -> {
            MethodDescriptorProperty methodDescriptorProperty = MethodDescriptorUtil.parseMethodDescriptorProperties(method);
            methodDescriptorProperty.setGrpcServiceName(grpcServiceProperty.getServiceName());
            methodDescriptorProperty.setGrpcFullMethodName(MethodDescriptor.generateFullMethodName(grpcServiceProperty.getServiceName(), methodDescriptorProperty.getGrpcMethodName()));

            methodDescriptorProperty.setProxyMethod(method);
            methodDescriptorProperty.setProxyBean(bean);

            methodDescriptorPropertyList.add(methodDescriptorProperty);
        });



        grpcProviderServerConfig.addMethodDesc(grpcServiceProperty.getServerPort().toString(), grpcServiceProperty.getServiceName(), methodDescriptorPropertyList);

        return bean;
    }
}
