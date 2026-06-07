package com.jkky.blog.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfigurationPackage(basePackages = {"com.jkky.blog.api", "com.jkky.blog.domain"})
@SpringBootApplication(scanBasePackages = "com.jkky.blog")
@ConfigurationPropertiesScan(basePackages = "com.jkky.blog")
@EnableJpaRepositories(basePackages = "com.jkky.blog.domain")
public class BlogApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApiApplication.class, args);
	}
}
