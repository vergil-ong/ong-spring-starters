package com.github.ong.grpc.example.service;

import com.github.ong.grpc.annotation.GrpcMethod;
import com.github.ong.grpc.annotation.GrpcService;
import com.github.ong.grpc.example.qo.user.UserQo;
import com.github.ong.grpc.example.vo.user.UserVo;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 15:40
 * @Description grpc 服务
 */
@GrpcService(serverName = "localhost", serverPort = 8001, serviceName = "service.user.UserService2")
public interface UserService2 {

    @GrpcMethod(methodName = "GetUser")
    UserVo getUser(UserQo userQo);
}
