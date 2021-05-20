package com.github.ong.grpc.pool;

import com.github.ong.grpc.GrpcConnection;
import com.github.ong.grpc.config.grpc.GrpcProperties;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 15:53
 * @Description 连接池工厂
 */
@Getter
@Setter
public class GrpcConFactory extends BasePooledObjectFactory<GrpcConnection> {

    private GrpcProperties grpcProperties;

    private List<ClientInterceptor> clientInterceptorList;

    @Override
    public GrpcConnection create() throws Exception {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(grpcProperties.getHost(), grpcProperties.getPort())
                .usePlaintext();
        if (!CollectionUtils.isEmpty(clientInterceptorList)){
            builder.intercept(clientInterceptorList.stream().toArray(ClientInterceptor[]::new));
        }

        return new GrpcConnection(builder);
    }

    @Override
    public PooledObject<GrpcConnection> wrap(GrpcConnection grpcConnection) {
        return new DefaultPooledObject<>(grpcConnection);
    }

    @Override
    public void destroyObject(PooledObject<GrpcConnection> pooledObject, DestroyMode mode) throws Exception {
        pooledObject.getObject().shutdown();
        super.destroyObject(pooledObject, mode);
    }
}
