package de.unistuttgart.iste.rss.bugminer.model;


import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * An abstract superclass for all objects that can be persisted in a data base
 */
@MappedSuperclass
public abstract class BaseEntity {
	@Id
	private String id;

	protected BaseEntity() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	/**
	 * Indicates whether some object represents the same persistable object as this one. Two objects
	 * are considered equal if their ids are the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof BaseEntity)) {
			return false;
		}

		BaseEntity other = (BaseEntity) obj;
		return getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override public String toString() {
		return String.format("%s#%s", getClass().getName(), id);
	}
}
