package com.github.ong.grpc.scanner;

import com.github.ong.grpc.annotation.GrpcProvider;
import com.github.ong.grpc.config.grpc.GrpcProviderServerConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/18 17:06
 * @Description grpc provider scanner
 */
@Slf4j
@Setter
@Deprecated
public class ClassPathGrpcProviderScanner extends ClassPathBeanDefinitionScanner {

    private BeanFactory beanFactory;

    public ClassPathGrpcProviderScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolderSet = super.doScan(basePackages);
        if (CollectionUtils.isEmpty(beanDefinitionHolderSet)){
            log.warn("beanDefinitionHolderSet is Empty, no grpc provider basePackages are {}", basePackages);
            return beanDefinitionHolderSet;
        }

        GrpcProviderServerConfig grpcProviderServerConfig = beanFactory.getBean(GrpcProviderServerConfig.class);
        parseGrpcProvider(beanDefinitionHolderSet, grpcProviderServerConfig);

        return beanDefinitionHolderSet;
    }

    private void parseGrpcProvider(Set<BeanDefinitionHolder> beanDefinitionHolderSet,
                                   GrpcProviderServerConfig grpcProviderServerConfig){
        for (BeanDefinitionHolder holder : beanDefinitionHolderSet){
            BeanDefinition beanDefinition = holder.getBeanDefinition();
            if (!(beanDefinition instanceof GenericBeanDefinition)){
                continue;
            }

            GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) beanDefinition;
            Class<?> beanClass = genericBeanDefinition.getBeanClass();

            GrpcProvider grpcProvider = beanClass.getAnnotation(GrpcProvider.class);
            grpcProviderServerConfig.addService(grpcProvider.serverPort());
        }
    }
}
