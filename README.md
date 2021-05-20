# 一些 springboot 的starters
## spring-boot-starter-grpc-client
用于grpc 的客户端

用户 引入 此项目之后，可以实现grpc 的服务调用
1. 使用 @GrpcService 注解 grpc 服务的接口
2. 使用 @GrpcMethod 注解 gprc 服务中的方法   
3. 启动springboot 后 spring容器注入 接口，即可实现对 grpc 服务的调用

接口声明
```java
@GrpcService(serverName = "localhost", serverPort = 8000, serviceName = "service.user.UserService")
public interface UserService {

    @GrpcMethod(methodName = "GetUser")
    UserVo getUser(UserQo userQo);
}
```
接口注入
```java
    @Resource 
    private UserService userService;
```

## spring-boot-starter-grpc-server
用于 grpc 的服务端

用户 引入 此项目之后，可以实现grpc 服务的声明，即暴露端口对外提供grpc 服务
1. 使用 @GrpcProvider 注解 grpc 服务的接口
2. 使用 @GrpcMethod 注解 grpc 服务中的方法  
   ps: 以上两步 可以 同 grpc 客户端使用同一个 interface实现，即一份接口声明，两个模块分别负责调用和实现  
   见 examples/example-core
3. 启动springboot 后 starter 会扫描所有@GrpcProvider 注解过的类，提供grpc服务

```java
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
```

## examples
一些示例
