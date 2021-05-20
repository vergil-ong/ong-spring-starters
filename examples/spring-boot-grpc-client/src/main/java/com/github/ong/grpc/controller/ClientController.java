package com.github.ong.grpc.controller;

import com.github.ong.grpc.example.qo.user.UserQo;
import com.github.ong.grpc.example.service.UserService;
import com.github.ong.grpc.example.service.UserService2;
import com.github.ong.grpc.example.vo.user.UserVo;
import com.github.ong.grpc.marshaller.ProtoMarshaller;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/client")
public class ClientController {

    @Resource
    private UserService userService;

    @Resource
    private UserService2 userService2;

    @GetMapping("/hello1")
    public String hello1(){
        log.info("hello1");

        return "hello1 "+ LocalDateTime.now();
    }

    @GetMapping("/getUser1")
    public UserVo getUser1(UserQo userQo){
        log.info("userQo {}", userQo);

        String getUserFullMethodName = "service.user.UserService/GetUser";

        MethodDescriptor.Builder<Object, Object> builder = MethodDescriptor.newBuilder(
                new ProtoMarshaller(UserQo.class),
                new ProtoMarshaller(UserVo.class)
        ).setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(getUserFullMethodName);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8000)
                .usePlaintext()
                .build();

        ClientCall<Object, Object> clientCall = channel.newCall(builder.build(), CallOptions.DEFAULT);

        UserVo response = (UserVo) ClientCalls.blockingUnaryCall(clientCall, userQo);

        log.info("response {}", response);

        return response;
    }

    @GetMapping("/getUser2")
    public List<UserVo> getUser(UserQo userQo){
        log.info("userQo {} {}", userQo.getQueryMsg1(), userQo.getAge());

        List<UserVo> userVoList = new ArrayList<>();

        getUserVo(userService.getUser(userQo), userVoList);
        getUserVo(userService2.getUser(userQo), userVoList);

        return userVoList;
    }

    private void getUserVo(UserVo userVo, List<UserVo> userVoList){
        log.info("userVo {}", userVo);

        userVoList.add(userVo);

    }
}
