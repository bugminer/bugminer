package de.unistuttgart.iste.rss.bugminer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Identifies a path as the path to a directory for permanent storage
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface DataDirectory {
}
