package com.github.ong.grpc.util;

import com.github.ong.grpc.annotation.GrpcProvider;
import com.github.ong.grpc.annotation.GrpcService;
import com.github.ong.grpc.constant.GrpcConfigConst;
import com.github.ong.grpc.property.GrpcServiceProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/20 14:14
 * @Description grpc 服务注解工具类
 */
public class GrpcServiceUtil {

    public static GrpcServiceProperty getGrpcServiceProperty(Class<?> beanClass){
        GrpcProvider grpcProvider = beanClass.getAnnotation(GrpcProvider.class);
        List<GrpcService> grpcServiceList = new ArrayList<>();

        List<Class<?>> superclassList = ClassUtils.getAllInterfaces(beanClass);
        for (Class<?> superClass : superclassList){
            GrpcService grpcService = superClass.getAnnotation(GrpcService.class);
            if (Objects.isNull(grpcService)){
                continue;
            }

            grpcServiceList.add(grpcService);
        }

        GrpcServiceProperty grpcServiceProperty = getGrpcServiceProperty(grpcProvider, grpcServiceList);

        return grpcServiceProperty;
    }

    private static GrpcServiceProperty getGrpcServiceProperty(GrpcProvider grpcProvider, List<GrpcService> grpcServiceList){
        GrpcServiceProperty grpcServiceProperty = new GrpcServiceProperty();

        if (CollectionUtils.isEmpty(grpcServiceList)){
            grpcServiceProperty.setServiceName(grpcProvider.serviceName());
            grpcServiceProperty.setServerPort(grpcProvider.serverPort());
            return grpcServiceProperty;
        }

        grpcServiceProperty.setServiceName(grpcProvider.serviceName());
        grpcServiceProperty.setServerPort(grpcProvider.serverPort());

        boolean defaultServerPort = Objects.equals(grpcProvider.serverPort(), GrpcConfigConst.DEFAULT_GRPC_SERVER_PORT);

        for (GrpcService grpcService : grpcServiceList){
            if (StringUtils.isBlank(grpcServiceProperty.getServiceName())){
                grpcServiceProperty.setServiceName(grpcService.serviceName());
            }

            if (defaultServerPort){
                grpcServiceProperty.setServerPort(grpcService.serverPort());
                defaultServerPort = false;
            }
        }

        return grpcServiceProperty;
    }

}
