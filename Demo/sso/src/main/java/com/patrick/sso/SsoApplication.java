package com.patrick.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.patrick.sso.common.InputStreamHttpMessageConverter;

@SpringBootApplication
public class SsoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoApplication.class, args);
	}

	@Bean
	public RestTemplate buildRestTemplate(RestTemplateBuilder builder) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new InputStreamHttpMessageConverter());
		return restTemplate;
	}
}
