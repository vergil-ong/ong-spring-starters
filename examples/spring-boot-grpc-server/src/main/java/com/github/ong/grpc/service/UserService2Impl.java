package com.github.ong.grpc.service;

import com.github.ong.grpc.annotation.GrpcProvider;
import com.github.ong.grpc.example.qo.user.UserQo;
import com.github.ong.grpc.example.service.UserService;
import com.github.ong.grpc.example.service.UserService2;
import com.github.ong.grpc.example.vo.user.UserVo;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 18:14
 * @Description grpc 实现类
 */
@Slf4j
@GrpcProvider
public class UserService2Impl implements UserService2 {

    @Override
    public UserVo getUser(UserQo userQo) {

        log.info("userQo  {}", userQo);

        String resultMsg = "user service 2 result is " + LocalDateTime.now();
        log.info("resultMsg  {}", resultMsg);

        UserVo userVo = new UserVo();
        userVo.setResultMsg(resultMsg);

        return userVo;
    }
}
