package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class User {
	@OneToMany
	private Collection<Classification> classifications;
}
