package de.unistuttgart.iste.rss.bugminer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EntityFactory {
	@Autowired
	private ApplicationContext applicationContext;

	public <T> T make(Class<T> entityClass) {
		return applicationContext.getBean(entityClass);
	}
}
