package com.jkky.blog.api.auth.controller;

import com.jkky.blog.api.auth.dto.AdminUserResponse;
import com.jkky.blog.api.auth.dto.CsrfTokenResponse;
import com.jkky.blog.api.auth.dto.LoginRequest;
import com.jkky.blog.api.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/csrf")
	public CsrfTokenResponse csrf(CsrfToken csrfToken) {
		return CsrfTokenResponse.builder()
			.headerName(csrfToken.getHeaderName())
			.token(csrfToken.getToken())
			.build();
	}

	@PostMapping("/login")
	public AdminUserResponse login(
		@Valid @RequestBody LoginRequest request,
		HttpServletRequest httpServletRequest
	) {
		return authService.login(request, httpServletRequest);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public AdminUserResponse me() {
		return authService.getCurrentAdmin();
	}
}
