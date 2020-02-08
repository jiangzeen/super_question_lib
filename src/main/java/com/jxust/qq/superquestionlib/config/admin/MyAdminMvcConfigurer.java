package com.jxust.qq.superquestionlib.config.admin;

import com.jxust.qq.superquestionlib.controller.admin.Interceptor.AdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置后端管理的拦截器
 */
@Configuration
public class MyAdminMvcConfigurer implements WebMvcConfigurer
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截url
        InterceptorRegistration registration=registry.addInterceptor(new AdminInterceptor());
        registration.addPathPatterns("/admin/**");
        registration.excludePathPatterns("/admin/login");
    }
}
