package com.datatom.dspool.config;

import com.datatom.dspool.interceptor.DataInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Configuration
@EnableWebMvc
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DataInterceptor());
    }
}
