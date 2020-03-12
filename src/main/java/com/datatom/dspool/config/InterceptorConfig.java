package com.datatom.dspool.config;

import com.datatom.dspool.interceptor.DataInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description 拦截器配置类
 */
@Configuration
@EnableWebMvc
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new DataInterceptor());
        // 配置需要拦截的路径
        interceptorRegistration.addPathPatterns("/pool/**");
        interceptorRegistration.excludePathPatterns("/pool/checkConnect");

    }
}
