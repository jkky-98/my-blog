package com.jkky.blog.api.auth.support;

import com.jkky.blog.api.auth.config.AdminProperties;
import com.jkky.blog.domain.auth.entity.AdminUser;
import com.jkky.blog.domain.auth.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

	private final AdminProperties adminProperties;
	private final AdminUserRepository adminUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		adminUserRepository.findByUsername(adminProperties.username())
			.ifPresentOrElse(this::updateAdmin, this::createAdmin);
	}

	private void createAdmin() {
		AdminUser adminUser = AdminUser.builder()
			.username(adminProperties.username())
			.passwordHash(passwordEncoder.encode(adminProperties.password()))
			.displayName(adminProperties.displayName())
			.build();

		adminUserRepository.save(adminUser);
	}

	private void updateAdmin(AdminUser adminUser) {
		if (!passwordEncoder.matches(adminProperties.password(), adminUser.getPasswordHash())) {
			adminUser.updatePasswordHash(passwordEncoder.encode(adminProperties.password()));
		}
		adminUser.updateDisplayName(adminProperties.displayName());
	}
}
