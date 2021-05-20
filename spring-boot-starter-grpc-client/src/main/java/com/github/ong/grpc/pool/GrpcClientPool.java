package com.github.ong.grpc.pool;

import com.github.ong.constant.asserts.AssertMessageFormat;
import com.github.ong.grpc.GrpcConnection;
import com.github.ong.grpc.config.grpc.GrpcPoolProperties;
import com.github.ong.grpc.config.grpc.GrpcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 15:58
 * @Description grpc 连接池对象
 */
@Getter
@Setter
@Slf4j
public class GrpcClientPool {

    private GrpcConFactory grpcConFactory;

    private GrpcProperties grpcProperties;

    private GrpcPoolProperties grpcPoolProperties;

    private GenericObjectPool<GrpcConnection> grpcPool;

    public GrpcClientPool(GrpcConFactory grpcConFactory, GrpcProperties grpcProperties, GrpcPoolProperties grpcPoolProperties) {
        this.grpcConFactory = grpcConFactory;
        this.grpcProperties = grpcProperties;
        this.grpcPoolProperties = grpcPoolProperties;
    }

    public void initPool(){
        Assert.notNull(grpcConFactory, AssertMessageFormat.getNotNullMsg("grpcConFactory"));
        Assert.notNull(grpcProperties, AssertMessageFormat.getNotNullMsg("grpcProperties"));
        Assert.notNull(grpcPoolProperties, AssertMessageFormat.getNotNullMsg("grpcPoolProperties"));

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(grpcPoolProperties.getMaxTotal());
        poolConfig.setMinIdle(grpcPoolProperties.getMinIdle());
        poolConfig.setMaxIdle(grpcPoolProperties.getMaxIdle());
        poolConfig.setMaxWaitMillis(grpcPoolProperties.getMaxWaitMillis());
        poolConfig.setLifo(true);
        poolConfig.setMinEvictableIdleTimeMillis(grpcPoolProperties.getMinEvictableIdleTimeMillis());
        poolConfig.setBlockWhenExhausted(true);

        grpcConFactory.setGrpcProperties(grpcProperties);

        grpcPool = new GenericObjectPool<>(grpcConFactory, poolConfig);
    }

    private void checkAndInitPool(){
        if (Objects.isNull(grpcPool)){
            initPool();
        }
    }

    public GrpcConnection borrowConn(){
        checkAndInitPool();
        try {
            GrpcConnection grpcConnection = grpcPool.borrowObject();
            log.debug("总线程数 {}, 当前线程 {}", grpcPool.getCreatedCount(), grpcConnection);
            return grpcConnection;
        } catch (Exception e) {
            log.error("borrowConn Exception {}", e);
            return createConn();
        }
    }

    private GrpcConnection createConn(){
        checkAndInitPool();
        try {
            return grpcConFactory.create();
        } catch (Exception e) {
            log.error("createConn Exception {}", e);
            return null;
        }
    }

    public <T> T execute(GrpcRequestCallBack<T> requestCallBack){

        GrpcConnection grpcConnection = borrowConn();

        try {
            return requestCallBack.doWithGrpcConn(grpcConnection);
        } finally {
            grpcPool.returnObject(grpcConnection);
        }
    }
}
