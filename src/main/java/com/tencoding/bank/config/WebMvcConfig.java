package com.tencoding.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component // IoC 대상 - 2개 이상의 빈을 등록해야 할 때 사용
public class WebMvcConfig implements WebMvcConfigurer {
	
	// DI 처리 (의존성 주입)
	@Autowired
	private AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
		.addPathPatterns("/account/**")
		.addPathPatterns("/auth/**");
	}
	
	@Bean // IoC 관리 대상 -> 싱글톤 
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
