package com.github.ong.grpc.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 18:29
 * @Description rpc factory bean
 */
@Getter
@Setter
public class GrpcClientFactoryBean implements FactoryBean {

    private BeanFactory beanFactory;

    private Class<?> targetClassName;


    @Override
    public Object getObject() throws Exception {

        GrpcClientFactory grpcClientFactory = beanFactory.getBean(GrpcClientFactory.class);
        return grpcClientFactory.getClientBean(targetClassName);
    }

    @Override
    public Class<?> getObjectType() {
        return targetClassName;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
