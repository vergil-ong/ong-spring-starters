package com.github.ong.grpc.example.qo.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 15:51
 * @Description 请求参数
 */
@Setter
@Getter
public class UserQo {

    private String queryMsg1;

    private Integer age;
}
