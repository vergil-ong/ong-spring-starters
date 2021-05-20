package com.github.ong.grpc.proxy;

import com.github.ong.grpc.pool.GrpcClientPool;
import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import com.github.ong.grpc.util.MethodDescriptorUtil;
import com.github.ong.util.ArrayUtil;
import com.github.ong.util.ClassUtil;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 16:46
 * @Description channel 动态代理类
 */
@Slf4j
public class GrpcChannelProxy implements InvocationHandler {

    private GrpcClientPool grpcClientPool;

    private Map<String, MethodDescriptorProperty> grpcMethodPropertiesMap = new HashMap<>();

    public GrpcChannelProxy(GrpcClientPool grpcClientPool) {
        this.grpcClientPool = grpcClientPool;
    }

    public Map<String, MethodDescriptorProperty> addGrpcMethodProperties(MethodDescriptorProperty methodDescriptorProperty){
        grpcMethodPropertiesMap.put(methodDescriptorProperty.getGrpcMethodName(), methodDescriptorProperty);
        return grpcMethodPropertiesMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object response = grpcClientPool.execute(grpcConnection -> {

            String methodName = MethodDescriptorUtil.getMethodName(method);
            if (!grpcMethodPropertiesMap.containsKey(methodName)){
                log.warn("cannot find grpcMethod Definition {} {} return null", proxy, method);
                return null;
            }

            MethodDescriptorProperty methodDescriptorProperty = grpcMethodPropertiesMap.get(methodName);

            ManagedChannel channel = grpcConnection.getChannel();
            ClientCall<Object, Object> clientCall = buildClientCall(channel, methodDescriptorProperty);

            switch (methodDescriptorProperty.getMethodType()) {
                case UNARY:
                    if (method.getReturnType() == ListenableFuture.class) {
                        return ClientCalls.futureUnaryCall(clientCall, ArrayUtil.getObj(args, 0));
                    } else if (method.getReturnType().getName().equals("void")) {
                        if (ClassUtil.checkMethodHasParamType(method, StreamObserver.class)) {
                            ClientCalls.asyncUnaryCall(clientCall, ArrayUtil.getObj(args, 0), (StreamObserver<Object>) ArrayUtil.getObj(args, 1));
                            return null;
                        } else {
                            ClientCalls.blockingUnaryCall(clientCall, ArrayUtil.getObj(args, 0));
                            return null;
                        }
                    }
                    return ClientCalls.blockingUnaryCall(clientCall, ArrayUtil.getObj(args, 0));
                case BIDI_STREAMING:
                    return ClientCalls.asyncBidiStreamingCall(clientCall, (StreamObserver<Object>) ArrayUtil.getObj(args, 0));
                case CLIENT_STREAMING:
                    return ClientCalls.asyncClientStreamingCall(clientCall, (StreamObserver<Object>) ArrayUtil.getObj(args, 0));
                case SERVER_STREAMING:
                    return ClientCalls.blockingServerStreamingCall(clientCall, ArrayUtil.getObj(args, 0));
                case UNKNOWN:
                default:
                    return null;
            }
        });
        return response;
    }



    private ClientCall<Object, Object> buildClientCall(ManagedChannel channel, MethodDescriptorProperty methodDescriptorProperty){
        MethodDescriptor<Object, Object> methodDescriptor = MethodDescriptorUtil.buildMethodDescriptor(methodDescriptorProperty);
        ClientCall<Object, Object> clientCall = channel.newCall(methodDescriptor, CallOptions.DEFAULT);

        return clientCall;
    }
}
