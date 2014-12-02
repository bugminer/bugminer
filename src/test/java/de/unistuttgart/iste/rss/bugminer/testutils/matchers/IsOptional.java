package de.unistuttgart.iste.rss.bugminer.testutils.matchers;

import java.util.Objects;
import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;



/**
 * Provides matchers for the {@link Optional} values.
 *
 * @param <T> type of value in optional
 * @see Optional
 */
public class IsOptional<T> extends TypeSafeMatcher<Optional<? extends T>> {

	private final boolean someExpected;
	private final Optional<T> expected;
	private final Optional<Matcher<T>> matcher;

	private IsOptional(boolean someExpected) {
		this.someExpected = someExpected;
		this.expected = Optional.empty();
		this.matcher = Optional.empty();
	}

	private IsOptional(T value) {
		this.someExpected = true;
		this.expected = Optional.of(value);
		this.matcher = Optional.empty();
	}

	private IsOptional(Matcher<T> matcher) {
		this.someExpected = true;
		this.expected = Optional.empty();
		this.matcher = Optional.of(matcher);
	}

	@Override
	public void describeTo(Description description) {
		if (!someExpected) {
			description.appendText("<Absent>");
		} else if (expected.isPresent()) {
			description.appendValue(expected);
		} else if (matcher.isPresent()) {
			description.appendText("a present value matching ");
			matcher.get().describeTo(description);
		} else {
			description.appendText("<Present>");
		}
	}

	@Override
	public boolean matchesSafely(Optional<? extends T> item) {
		if (!someExpected) {
			return !item.isPresent();
		} else if (expected.isPresent()) {
			return item.isPresent() && Objects.equals(item.get(), expected.get());
		} else if (matcher.isPresent()) {
			return item.isPresent() && matcher.get().matches(item.get());
		} else {
			return item.isPresent();
		}
	}

	/**
	 * Checks that the passed Optional is not present.
	 */
	public static IsOptional<Object> isAbsent() {
		return new IsOptional<>(false);
	}

	/**
	 * Checks that the passed Optional is present.
	 */
	public static IsOptional<Object> isPresent() {
		return new IsOptional<>(true);
	}

	/**
	 * Checks that the passed Optional is Some and contains value matches {@code value} based on
	 * {@code Objects.equal}.
	 *
	 * @see Objects#equal(Object, Object)
	 */
	public static <T> IsOptional<T> isValue(T value) {
		return new IsOptional<>(value);
	}

	/**
	 * Checks that the passed Option is Some and contains value matches {@code value} based on given
	 * matcher.
	 */
	public static <T> IsOptional<T> isValue(Matcher<T> matcher) {
		return new IsOptional<>(matcher);
	}
}
