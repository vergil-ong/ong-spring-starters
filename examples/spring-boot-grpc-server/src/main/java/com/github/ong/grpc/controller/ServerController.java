package com.github.ong.grpc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/hello1")
    public String hello1(){
        log.info("hello1");

        return "hello1 "+ LocalDateTime.now();
    }
}
