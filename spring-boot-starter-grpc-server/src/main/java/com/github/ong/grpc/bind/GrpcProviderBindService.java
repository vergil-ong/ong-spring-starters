package com.github.ong.grpc.bind;

import com.github.ong.grpc.marshaller.MethodDescriptorProperty;
import com.github.ong.grpc.util.MethodDescriptorUtil;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/19 11:57
 * @Description 服务实现
 */
public class GrpcProviderBindService  implements BindableService {

    private String serviceName;

    private List<MethodDescriptorProperty> methodDescriptorPropertyList;

    public GrpcProviderBindService(String serviceName, List<MethodDescriptorProperty> methodDescriptorPropertyList) {
        this.serviceName = serviceName;
        this.methodDescriptorPropertyList = methodDescriptorPropertyList;
    }

    @Override
    public ServerServiceDefinition bindService() {
        if (!checkBindService()){
            return null;
        }

        ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(serviceName);
        for (MethodDescriptorProperty methodDescriptorProperty : methodDescriptorPropertyList){
            builder.addMethod(MethodDescriptorUtil.buildMethodDescriptor(methodDescriptorProperty), MethodDescriptorUtil.buildServerCallHandler(methodDescriptorProperty));
        }

        return builder.build();
    }

    private boolean checkBindService(){
        if (Objects.isNull(serviceName)){
            return false;
        }

        if (CollectionUtils.isEmpty(methodDescriptorPropertyList)){
            return false;
        }

        return true;
    }
}
