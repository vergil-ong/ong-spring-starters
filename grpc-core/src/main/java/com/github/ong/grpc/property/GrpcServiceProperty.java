package com.github.ong.grpc.property;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/20 14:15
 * @Description grpc 服务属性
 */
@Getter
@Setter
public class GrpcServiceProperty {

    private String serviceName;

    private Integer serverPort;
}
