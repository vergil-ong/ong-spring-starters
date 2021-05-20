package com.github.ong.grpc.config.grpc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/13 15:57
 * @Description grpc 连接池 配置
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "grpc.client.pool")
public class GrpcPoolProperties {

    private Integer maxTotal = 16;

    private Integer minIdle = 0;

    private Integer maxIdle = 60;

    private Long maxWaitMillis = -1L;

    private Long minEvictableIdleTimeMillis = 1000L * 60L;
}
