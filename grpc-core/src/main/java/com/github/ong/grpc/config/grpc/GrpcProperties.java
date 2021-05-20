package com.github.ong.grpc.config.grpc;

import com.github.ong.grpc.annotation.GrpcService;
import com.github.ong.grpc.constant.GrpcConfigConst;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 15:54
 * @Description grpc 配置
 */
@Getter
@Setter
public class GrpcProperties {

    private String host = GrpcConfigConst.DEFAULT_GRPC_SERVER_HOST;

    private Integer port = GrpcConfigConst.DEFAULT_GRPC_SERVER_PORT;

    private static final String D_LIMIT = "_";

    public GrpcProperties() {
    }

    public GrpcProperties(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public GrpcProperties(GrpcService grpcService) {
        if (Objects.nonNull(grpcService)){
            this.host = grpcService.serverName();
            this.port = grpcService.serverPort();
        }
    }

    public String getGroupKey(){
        return getGroupKey(getHost(), getPort());
    }

    public static String getGroupKey(GrpcService grpcService){
        if (Objects.isNull(grpcService)){
            return null;
        }
        return getGroupKey(grpcService.serverName(), grpcService.serverPort());
    }

    public static String getGroupKey(String serverName, Integer port){
        return String.join(D_LIMIT, serverName, port.toString());
    }
}
