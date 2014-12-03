package de.unistuttgart.iste.rss.bugminer.strategies;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;

/**
 * Some classes annotated with {@link Strategy} produce an invalid configuration. One instance would
 * be two strategies for the same interface with the same name
 */
public class StrategyConfigurationException extends RuntimeException {
	private static final long serialVersionUID = -7214248649514336067L;

	/**
	 * Constructs an <code>StrategyConfigurationException</code> with no detail message.
	 */
	public StrategyConfigurationException() {
		super();
	}

	/**
	 * Constructs an <code>StrategyConfigurationException</code> with the specified detail message.
	 *
	 * @param s the detail message.
	 */
	public StrategyConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 *
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically
	 * incorporated in this exception's detail message.
	 *
	 * @param message the detail message (which is saved for later retrieval by the
	 *        {@link Throwable#getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the
	 *        {@link Throwable#getCause()} method). (A <tt>null</tt> value is permitted, and
	 *        indicates that the cause is nonexistent or unknown.)
	 * @since 1.5
	 */
	public StrategyConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and
	 * detail message of <tt>cause</tt>). This constructor is useful for exceptions that are little
	 * more than wrappers for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause the cause (which is saved for later retrieval by the
	 *        {@link Throwable#getCause()} method). (A <tt>null</tt> value is permitted, and
	 *        indicates that the cause is nonexistent or unknown.)
	 * @since 1.5
	 */
	public StrategyConfigurationException(Throwable cause) {
		super(cause);
	}
}
