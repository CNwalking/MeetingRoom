package com.walking.meeting.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor())
                .addPathPatterns("/**")   // 拦截除了swagger以外的所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }
    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor();
    }
}
