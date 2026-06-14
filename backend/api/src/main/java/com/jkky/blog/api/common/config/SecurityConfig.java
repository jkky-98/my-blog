package com.jkky.blog.api.common.config;

import com.jkky.blog.api.common.error.ErrorCode;
import com.jkky.blog.api.common.error.ErrorResponse;
import com.jkky.blog.domain.auth.repository.AdminUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
		http
			.csrf(csrf -> csrf
				.csrfTokenRepository(new HttpSessionCsrfTokenRepository())
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(
					"/api/posts/**",
					"/api/categories",
					"/api/tags",
					"/api/projects",
					"/api/auth/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint((request, response, exception) ->
					writeErrorResponse(response, objectMapper, ErrorCode.UNAUTHENTICATED)
				)
				.accessDeniedHandler((request, response, exception) -> {
					ErrorCode errorCode = exception instanceof CsrfException
						? ErrorCode.CSRF_TOKEN_INVALID
						: ErrorCode.ACCESS_DENIED;
					writeErrorResponse(response, objectMapper, errorCode);
				})
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(AdminUserRepository adminUserRepository) {
		return username -> adminUserRepository.findByUsername(username)
			.map(adminUser -> User.builder()
				.username(adminUser.getUsername())
				.password(adminUser.getPasswordHash())
				.roles("ADMIN")
				.build())
			.orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));
	}

	private void writeErrorResponse(
		HttpServletResponse response,
		ObjectMapper objectMapper,
		ErrorCode errorCode
	) throws java.io.IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), ErrorResponse.of(errorCode));
	}
}
