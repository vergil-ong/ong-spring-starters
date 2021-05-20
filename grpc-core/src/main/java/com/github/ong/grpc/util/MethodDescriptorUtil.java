package com.github.ong.grpc.util;

import com.github.ong.asserts.Asserts;
import com.github.ong.grpc.annotation.GrpcMethod;
import com.github.ong.grpc.exception.GrpcMethodMatchException;
import com.github.ong.grpc.exception.GrpcServerCreateException;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import com.github.ong.grpc.marshaller.ProtoMarshaller;
import com.github.ong.util.ArrayUtil;
import com.github.ong.util.ClassUtil;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 16:59
 * @Description grpc 方法描述 工具类
 */
@Slf4j
public class MethodDescriptorUtil {

    public static String getMethodName(Method method){
        GrpcMethod gRpcMethod = MethodUtils.getAnnotation(method, GrpcMethod.class, true, false);
        Asserts.notNull(gRpcMethod, "grpcMethod cannot be null");

        return getMethodName(method, gRpcMethod);
    }

    private static String getMethodName(Method method, GrpcMethod gRpcMethod){
        String methodName = null;

        if (Objects.nonNull(gRpcMethod)){
            methodName = gRpcMethod.methodName();
        }

        if (StringUtils.isNoneBlank(methodName)){
            return methodName;
        }

        return method.getName();
    }

    public static MethodDescriptorProperty parseMethodDescriptorProperties(Method method){
        GrpcMethod gRpcMethod = MethodUtils.getAnnotation(method, GrpcMethod.class, true, false);
        Asserts.notNull(gRpcMethod, "grpcMethod cannot be null");

        MethodDescriptorProperty methodDescriptorProperty = new MethodDescriptorProperty();
        methodDescriptorProperty.setMethodType(gRpcMethod.methodType());

        String methodName = getMethodName(method, gRpcMethod);
        methodDescriptorProperty.setGrpcMethodName(methodName);

        Type requestType = parseRequestType(method, gRpcMethod);
        methodDescriptorProperty.setReqClass(requestType);

        Type returnType = parseReturnType(method, gRpcMethod);
        methodDescriptorProperty.setRespClass(returnType);

        return methodDescriptorProperty;
    }

    private static Type parseRequestType(Method method, GrpcMethod gRpcMethod){
        MethodDescriptor.MethodType methodType = MethodDescriptor.MethodType.UNARY;
        if (Objects.nonNull(gRpcMethod)){
            methodType = gRpcMethod.methodType();
        }

        Type[] methodParamTypes;
        Type[] returnTypes;
        switch (methodType) {
            case UNARY:
                if (method.getGenericParameterTypes().length == 2) {
                    checkTwoParamBeanStreamObserver(method);
                }

                methodParamTypes = method.getGenericParameterTypes();
                return ArrayUtil.getObj(methodParamTypes, 0);
            case BIDI_STREAMING:
                checkOneParamBeanStreamObserver(method);

                if (!ClassUtil.checkIfSupplierClass(method.getReturnType(), StreamObserver.class)){
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method return type have to be StreamObserver");
                }
                methodParamTypes = ClassUtil.getMethodParamTypes(method, StreamObserver.class);
                if (Objects.isNull(methodParamTypes)){
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method param have to be StreamObserver");
                }
                returnTypes = ClassUtil.getReturnTypes(method);
                return ArrayUtil.getObj(returnTypes, 0);
            case CLIENT_STREAMING:
                if (!ClassUtil.checkIfSupplierClass(method.getReturnType(), StreamObserver.class)){
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method return type have to be StreamObserver");
                }
                checkOneParamBeanStreamObserver(method);
                returnTypes = ClassUtil.getReturnTypes(method);
                return ArrayUtil.getObj(returnTypes, 0);
            case SERVER_STREAMING:

                if (method.getGenericParameterTypes().length == 2) {
                    checkTwoParamBeanStreamObserver(method);
                } else {
                    checkOneParamBeanStreamObserver(method);
                }

                methodParamTypes = method.getGenericParameterTypes();
                return ArrayUtil.getObj(methodParamTypes, 0);
            case UNKNOWN:
            default:
                throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                        "cannot parse grpc method ");
        }
    }

    private static Type parseReturnType(Method method, GrpcMethod gRpcMethod){
        MethodDescriptor.MethodType methodType = MethodDescriptor.MethodType.UNARY;
        if (Objects.nonNull(gRpcMethod)){
            methodType = gRpcMethod.methodType();
        }

        Type[] returnTypes;

        switch (methodType){
            case UNARY:
                Class<?> returnType = method.getReturnType();
                if (ListenableFuture.class.isAssignableFrom(returnType)){
                    Type[] actualTypes = ClassUtil.getActualTypes(returnType);
                    return ArrayUtil.getObj(actualTypes, 0);
                }

                //如果是void 则从参数里去返回值
                if (returnType.getName().equals(ClassUtil.CLASS_NAME_VOID)){
                    Type[] methodParamTypes = ClassUtil.getMethodParamTypes(method, StreamObserver.class);
                    if (Objects.isNull(methodParamTypes)){
                        return method.getGenericReturnType();
                    }

                    checkTwoParamBeanStreamObserver(method);

                    return ArrayUtil.getObj(methodParamTypes, 0);
                }

                return method.getGenericReturnType();
            case BIDI_STREAMING:
                checkOneParamBeanStreamObserver(method);
                if (!ClassUtil.checkIfSupplierClass(method.getReturnType(), StreamObserver.class)){
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method return type have to be StreamObserver");
                }

                Type[] methodParamTypes = ClassUtil.getMethodParamTypes(method, StreamObserver.class);
                if (Objects.isNull(methodParamTypes)){
                    //理论上 上面方法 checkOneParamBeanStreamObserver 已经校验了
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method param have to be StreamObserver");
                }

                returnTypes = ClassUtil.getReturnTypes(method);
                return ArrayUtil.getObj(returnTypes, 0);
            case CLIENT_STREAMING:
                if (!ClassUtil.checkIfSupplierClass(method.getReturnType(), StreamObserver.class)){
                    throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                            "method return type have to be StreamObserver");
                }
                checkOneParamBeanStreamObserver(method);
                returnTypes = ClassUtil.getReturnTypes(method);
                return ArrayUtil.getObj(returnTypes, 0);
            case SERVER_STREAMING:

                if (method.getGenericParameterTypes().length == 2) {
                    checkTwoParamBeanStreamObserver(method);
                } else {
                    checkOneParamBeanStreamObserver(method);
                }
                returnTypes = method.getGenericParameterTypes();
                return ArrayUtil.getObj(returnTypes, 0);
            case UNKNOWN:
            default:
                throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName(),
                        "cannot parse grpc method ");
        }
    }

    /**
     * 判断 方法只含有两个参数，第一个是 业务对象，第二个是 StreamObserver
     * @param method
     */
    private static void checkTwoParamBeanStreamObserver(Method method){
        Type[] types = method.getGenericParameterTypes();
        if (types == null || types.length != 2) {
            throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName());
        }

        Type type = ArrayUtil.getObj(types, 1);
        if (!ClassUtil.checkIfSupplierClass(type, StreamObserver.class)){
            throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName());
        }
    }

    /**
     * 判断 方法只含有一个参数是 StreamObserver
     * @param method
     */
    private static void checkOneParamBeanStreamObserver(Method method){
        Type[] types = method.getGenericParameterTypes();
        if (types.length != 1) {
            throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName());
        }

        Type type = ArrayUtil.getObj(types, 0);
        if (!ClassUtil.checkIfSupplierClass(type, StreamObserver.class)){
            throw new GrpcMethodMatchException(method.getDeclaringClass().getName(), method.getName());
        }
    }


    public static MethodDescriptor<Object, Object> buildMethodDescriptor(MethodDescriptorProperty methodDescriptorProperty){

        MethodDescriptor.Builder<Object, Object> builder = MethodDescriptor.newBuilder(
                new ProtoMarshaller(methodDescriptorProperty.getReqClass()),
                new ProtoMarshaller(methodDescriptorProperty.getRespClass())
        )
                .setType(methodDescriptorProperty.getMethodType())
                .setFullMethodName(methodDescriptorProperty.getGrpcFullMethodName());


        return builder.build();
    }

    private static void asyncInvokeMethod(Object bean, Method method,
                                            Object request, StreamObserver<Object> streamObserver) throws InvocationTargetException, IllegalAccessException {
        int paramSize = method.getGenericParameterTypes().length;

        if (paramSize == 0){
            Object response = method.invoke(bean);
            streamObserver.onNext(response);
            streamObserver.onCompleted();
            return;
        }

        if (paramSize == 1){
            if (ClassUtil.checkMethodHasParamType(method, StreamObserver.class)){
                //由业务方 处理 stream observer
                method.invoke(bean, streamObserver);
            } else {
                Object response = method.invoke(bean, request);
                streamObserver.onNext(response);
                streamObserver.onCompleted();
            }
            return;
        }

        if (paramSize == 2){
            checkTwoParamBeanStreamObserver(method);
            method.invoke(bean, request, streamObserver);
        }
    }

    private static void handleCall(Object bean, Method method,
                                   Object request, StreamObserver<Object> responseObserver){
        try {
            asyncInvokeMethod(bean, method, request, responseObserver);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("invokeMethod error params {}, {}", bean, method);
            log.error("invokeMethod error", e);
            throw new StatusRuntimeException(Status.INTERNAL);
        }
    }

    private static StreamObserver<Object> invokeMethod(Object bean, Method method, StreamObserver responseObserver){
        try {
            return (StreamObserver<Object>) method.invoke(bean, responseObserver);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invokeMethod error params {}, {}", bean, method);
            log.error("invokeMethod error", e);
            throw new StatusRuntimeException(Status.INTERNAL);
        }
    }

    public static ServerCallHandler<Object, Object> buildServerCallHandler(MethodDescriptorProperty methodDescriptorProperty){
        Method method = methodDescriptorProperty.getProxyMethod();
        Object bean = methodDescriptorProperty.getProxyBean();

        switch (methodDescriptorProperty.getMethodType()){
            case UNARY:
                return ServerCalls.asyncUnaryCall((request, responseObserver) -> handleCall(bean, method, request, responseObserver));
            case SERVER_STREAMING:
                return ServerCalls.asyncServerStreamingCall((request, responseObserver) -> handleCall(bean, method, request, responseObserver));
            case CLIENT_STREAMING:
                return ServerCalls.asyncClientStreamingCall(responseObserver -> invokeMethod(bean, method, responseObserver));
            case BIDI_STREAMING:
                return ServerCalls.asyncBidiStreamingCall(responseObserver -> invokeMethod(bean, method, responseObserver));
            case UNKNOWN:
            default:
                throw new GrpcServerCreateException("cannot find grpc method type");
        }
    }
}
