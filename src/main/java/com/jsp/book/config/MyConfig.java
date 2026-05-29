package com.jsp.book.config;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@org.springframework.scheduling.annotation.EnableScheduling
public class MyConfig {

	@Bean
	SecureRandom secureRandom() {
		return new SecureRandom();
	}

}
