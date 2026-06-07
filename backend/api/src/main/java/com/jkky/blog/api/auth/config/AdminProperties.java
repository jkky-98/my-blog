package com.jkky.blog.api.auth.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "blog.admin")
public record AdminProperties(
	@NotBlank String username,
	@NotBlank String password,
	@NotBlank String displayName
) {
}
