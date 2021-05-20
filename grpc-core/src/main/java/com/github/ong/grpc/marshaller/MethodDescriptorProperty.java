package com.github.ong.grpc.marshaller;

import io.grpc.MethodDescriptor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 17:04
 * @Description 方法描述 属性
 */
@Getter
@Setter
public class MethodDescriptorProperty {

    private Type reqClass;

    private Type respClass;

    private MethodDescriptor.MethodType methodType;

    private String grpcMethodName;

    private String grpcServiceName;

    private String grpcFullMethodName;

    private Method proxyMethod;

    private Object proxyBean;

}
