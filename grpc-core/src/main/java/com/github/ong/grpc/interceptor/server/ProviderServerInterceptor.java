package com.github.ong.grpc.interceptor.server;


import com.github.ong.grpc.constant.GrpcConfigConst;
import io.grpc.ServerInterceptor;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/19 15:29
 * @Description server 拦截器 根据 server 配置 拦截器
 */
public interface ProviderServerInterceptor extends ServerInterceptor {

    /**
     * 获取 grpc 服务 绑定域名
     * @return
     */
    default String getServerName() {
        return GrpcConfigConst.DEFAULT_GRPC_SERVER_HOST;
    }

    /**
     * 获取 grpc 服务 端口
     * -1 代表 所有端口都应用此迭代器
     * @return
     */
    default Integer getPort(){
        return GrpcConfigConst.COMMON_SERVER_INTERCEPTOR_PORT;
    }
}
