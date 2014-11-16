package de.unistuttgart.iste.rss.bugminer.strategies;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;

@Component
public class StrategyFactory {

	@Autowired
	private ApplicationContext applicationContext;

	private Map<StrategyKey, Object> annotatedStrategies = new HashMap<StrategyKey, Object>();

	@PostConstruct
	public void init() {
		Collection<Object> strategies =
				applicationContext.getBeansWithAnnotation(Strategy.class).values();

		for (Object strategy : strategies) {
			Strategy strategyAnnotation =
					AnnotationUtils.findAnnotation(strategy.getClass(), Strategy.class);
			StrategyKey key = new StrategyKey(strategyAnnotation.type(), strategyAnnotation.name());

			if (annotatedStrategies.containsKey(key))
				throw new RuntimeException();

			annotatedStrategies.put(key, strategy);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getStrategy(Class<T> type, String name) {
		StrategyKey key = new StrategyKey(type, name);

		if (!annotatedStrategies.containsKey(key))
			throw new RuntimeException();

		return (T) annotatedStrategies.get(key);
	}

	private static class StrategyKey {
		public final Class<?> type;
		public final String name;

		public StrategyKey(Class<?> type, String name) {
			this.type = type;
			this.name = name;
		}

		@Override
		public boolean equals(Object obj) {
			StrategyKey key = (StrategyKey) obj;
			return this.type == key.type && this.name.equals(key.name);
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(type).append(name).toHashCode();
		}
	}
}
