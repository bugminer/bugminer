package de.unistuttgart.iste.rss.bugminer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Annotates a named strategy implementation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Strategy {
	/**
	 * The interface that specifies the strategy
	 */
	Class<?> type();

	/**
	 * The name of the strategy implementation. Must be unique within {@link #type()}
	 */
	String name();
}
