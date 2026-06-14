package com.jkky.blog.api.auth.service;

import com.jkky.blog.api.auth.dto.AdminUserResponse;
import com.jkky.blog.api.auth.dto.LoginRequest;
import com.jkky.blog.api.common.error.BlogException;
import com.jkky.blog.api.common.error.ErrorCode;
import com.jkky.blog.domain.auth.entity.AdminUser;
import com.jkky.blog.domain.auth.repository.AdminUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final AdminUserRepository adminUserRepository;

	@Transactional
	public AdminUserResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
		Authentication authentication = authenticate(request);

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		HttpSession session = httpServletRequest.getSession(true);
		httpServletRequest.changeSessionId();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

		return findAdminByUsername(authentication.getName());
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		new SecurityContextLogoutHandler().logout(request, response, authentication);
	}

	public AdminUserResponse getCurrentAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BlogException(ErrorCode.UNAUTHENTICATED);
		}

		return findAdminByUsername(authentication.getName());
	}

	private Authentication authenticate(LoginRequest request) {
		try {
			return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password())
			);
		} catch (AuthenticationException exception) {
			throw new BlogException(ErrorCode.INVALID_CREDENTIALS, exception);
		}
	}

	private AdminUserResponse findAdminByUsername(String username) {
		AdminUser adminUser = adminUserRepository.findByUsername(username)
			.orElseThrow(() -> new BlogException(ErrorCode.UNAUTHENTICATED));

		return AdminUserResponse.builder()
			.id(adminUser.getId())
			.username(adminUser.getUsername())
			.displayName(adminUser.getDisplayName())
			.build();
	}
}
