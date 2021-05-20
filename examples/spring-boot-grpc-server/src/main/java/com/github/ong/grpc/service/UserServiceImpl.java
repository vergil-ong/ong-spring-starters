package com.github.ong.grpc.service;

import com.github.ong.grpc.annotation.GrpcProvider;
import com.github.ong.grpc.example.qo.user.UserQo;
import com.github.ong.grpc.example.service.UserService;
import com.github.ong.grpc.example.vo.user.UserVo;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 18:14
 * @Description grpc 实现类
 */
@Slf4j
@GrpcProvider(serviceName = "service.user.UserService")
public class UserServiceImpl implements UserService {

    @Override
    public UserVo getUser(UserQo userQo) {

        log.info("userQo  {}", userQo);

        String resultMsg = "result is " + LocalDateTime.now();
        log.info("resultMsg  {}", resultMsg);

        UserVo userVo = new UserVo();
        userVo.setResultMsg(resultMsg);

        return userVo;
    }
}
