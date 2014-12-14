package de.unistuttgart.iste.rss.bugminer.strategies;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

	private final Map<StrategyKey, Object> annotatedStrategies = new HashMap<StrategyKey, Object>();

	protected StrategyFactory() {
		// managed bean
	}

	@PostConstruct
	protected void init() {
		Collection<Object> strategies =
				applicationContext.getBeansWithAnnotation(Strategy.class).values();

		for (Object strategy : strategies) {
			Strategy strategyAnnotation =
					AnnotationUtils.findAnnotation(strategy.getClass(), Strategy.class);
			StrategyKey key = new StrategyKey(strategyAnnotation.type(), strategyAnnotation.name());

			if (annotatedStrategies.containsKey(key)) {
				throw new StrategyConfigurationException(String.format(
						"There are two strategies called \"%s\" for the interface %s",
						strategyAnnotation.type().getName(), strategyAnnotation.name()));
			}

			annotatedStrategies.put(key, strategy);
		}
	}

	/**
	 * Gets a named strategy for the given interface
	 *
	 * @param type the interface the strategy implements
	 * @param name the strategy's name as specified in {@link Strategy}
	 * @return the strategy
	 * @throws IllegalArgumentException if there is no matching strategy defined
	 */
	public <T> T getStrategy(Class<T> type, String name) {
		return tryGetStrategy(type, name)
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"There is no strategy called \"%s\" for the interface %s", name,
						type.getName())));
	}

	/**
	 * Tries to find a named strategy for the given interface
	 *
	 * @param type the interface the strategy implements
	 * @param name the strategy's name as specified in {@link Strategy}
	 * @return the strategy if found, or {@link Optional#empty()} if no matching strategy defined
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<T> tryGetStrategy(Class<T> type, String name) {
		StrategyKey key = new StrategyKey(type, name);

		if (!annotatedStrategies.containsKey(key)) {
			return Optional.empty();
		}

		return Optional.of((T) annotatedStrategies.get(key));
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
			if (!(obj instanceof StrategyKey)) {
				return false;
			}
			StrategyKey key = (StrategyKey) obj;
			return this.type == key.type && this.name.equals(key.name);
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(type).append(name).toHashCode();
		}
	}
}
