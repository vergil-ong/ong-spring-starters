package com.github.ong.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/4/30 18:21
 * @Description grpc链接
 */
@Getter
@Setter
public class GrpcConnection {

    private ManagedChannel channel;

    public GrpcConnection(ManagedChannelBuilder<?> managedChannelBuilder) {
        this.channel = managedChannelBuilder.build();
    }

    public ManagedChannel shutdown(){
        if (Objects.nonNull(channel)){
            return channel.shutdown();
        }
        return null;
    }
}
