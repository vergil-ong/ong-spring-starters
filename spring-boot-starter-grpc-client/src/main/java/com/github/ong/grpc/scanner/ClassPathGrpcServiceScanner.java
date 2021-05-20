package com.github.ong.grpc.scanner;

import com.github.ong.grpc.factory.GrpcClientFactoryBean;
import com.github.ong.grpc.constant.GrpcConfigConst;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 18:18
 * @Description rpcservice 扫描器
 */
@Getter
@Setter
@Slf4j
public class ClassPathGrpcServiceScanner extends ClassPathBeanDefinitionScanner {

    private BeanFactory beanFactory;

    public ClassPathGrpcServiceScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolderSet = super.doScan(basePackages);
        if (CollectionUtils.isEmpty(beanDefinitionHolderSet)){
            log.warn("beanDefinitionHolderSet is Empty, no grpc service basePackages are {}", basePackages);
            return beanDefinitionHolderSet;
        }

        processGrpcBeanDefinitionHolderSet(beanDefinitionHolderSet, beanFactory);

        return beanDefinitionHolderSet;
    }

    private void processGrpcBeanDefinitionHolderSet(Set<BeanDefinitionHolder> beanDefinitionHolderSet,
                                                    BeanFactory beanFactory){

        for (BeanDefinitionHolder holder : beanDefinitionHolderSet){
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
            beanDefinition.getPropertyValues().addPropertyValue(GrpcConfigConst.BEAN_FACTORY, beanFactory);
            beanDefinition.getPropertyValues().addPropertyValue(GrpcConfigConst.TARGET_CLASS_NAME, beanDefinition.getBeanClassName());
            beanDefinition.setBeanClass(GrpcClientFactoryBean.class);
        }
    }
}
