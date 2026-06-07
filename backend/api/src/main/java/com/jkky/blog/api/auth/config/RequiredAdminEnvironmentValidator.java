package com.jkky.blog.api.auth.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RequiredAdminEnvironmentValidator implements BeanFactoryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		requireEnvironmentValue("blog.admin.username", "ADMIN_USERNAME");
		requireEnvironmentValue("blog.admin.password", "ADMIN_PASSWORD");
		requireEnvironmentValue("blog.admin.display-name", "ADMIN_DISPLAY_NAME");
	}

	private void requireEnvironmentValue(String propertyName, String environmentName) {
		String value;
		try {
			value = environment.getProperty(propertyName);
		} catch (IllegalArgumentException exception) {
			throw new BeanCreationException("Required environment variable is missing: " + environmentName, exception);
		}

		if (!StringUtils.hasText(value)) {
			throw new BeanCreationException("Required environment variable is missing: " + environmentName);
		}
	}
}
