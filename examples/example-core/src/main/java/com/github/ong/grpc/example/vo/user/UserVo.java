package com.github.ong.grpc.example.vo.user;

import com.github.ong.grpc.example.qo.user.UserQo;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 15:52
 * @Description 用户接口 响应参数
 */
@Getter
@Setter
public class UserVo {

    private UserQo userQo;

    private String resultMsg;

    private Integer ageSum;

    private Double ageAvg;

}
