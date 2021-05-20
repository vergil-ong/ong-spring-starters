package com.github.ong.grpc.pool;

import com.github.ong.grpc.GrpcConnection;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 16:10
 * @Description 请求回调
 */
public interface GrpcRequestCallBack<T> {

    /**
     * grpc处理函数封装，用于外部 统一进行 请求链接归还
     * @param grpcConnection
     * @return
     */
    T doWithGrpcConn(GrpcConnection grpcConnection);
}
