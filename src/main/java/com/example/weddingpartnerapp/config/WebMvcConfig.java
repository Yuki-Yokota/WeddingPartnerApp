package com.example.weddingpartnerapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.weddingpartnerapp.common.SessionUserInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionUserInterceptor())
		        .addPathPatterns("/**")           
		        .excludePathPatterns("/login") 
		        .excludePathPatterns("/questionnaire")
		        .excludePathPatterns("/css/**")         
		        .excludePathPatterns("/js/**");
    }
}