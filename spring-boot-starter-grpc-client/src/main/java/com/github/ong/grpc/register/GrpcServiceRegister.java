package com.github.ong.grpc.register;

import com.github.ong.grpc.annotation.GrpcService;
import com.github.ong.grpc.annotation.GrpcClientScan;
import com.github.ong.grpc.scanner.ClassPathGrpcServiceScanner;
import com.github.ong.grpc.constant.GrpcConfigConst;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * grpc 的调用端 服务端 都要对 接口信息进行读取
 *
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 17:22
 * @Description 注册 {@link com.github.ong.grpc.annotation.GrpcService} 注解的类
 */
@Slf4j
@Setter
public class GrpcServiceRegister implements BeanFactoryAware, ResourceLoaderAware, ImportBeanDefinitionRegistrar {

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(GrpcClientScan.class.getCanonicalName()));
        if (Objects.isNull(annotationAttributes)){
            log.warn("GrpcClientScan was not found");
            return;
        }

        ClassPathGrpcServiceScanner classPathGrpcServiceScanner = new ClassPathGrpcServiceScanner(registry, false);
        classPathGrpcServiceScanner.setBeanFactory(beanFactory);
        classPathGrpcServiceScanner.setResourceLoader(resourceLoader);
        classPathGrpcServiceScanner.addIncludeFilter(new AnnotationTypeFilter(GrpcService.class));
        List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        Set<String> basePackageSet = new HashSet<>(basePackages);

        Arrays.stream(annotationAttributes.getStringArray(GrpcConfigConst.SCAN_BASE_PACKAGE))
                .forEach(basePackage -> {
                    if (StringUtils.hasText(basePackage)){
                        basePackageSet.add(basePackage);
                    }
                });

        classPathGrpcServiceScanner.scan(StringUtils.toStringArray(basePackageSet));
    }
}
