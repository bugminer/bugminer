package de.unistuttgart.iste.rss.bugminer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides utility methods for {@link Map}
 */
public final class MapUtils {
	private MapUtils() {}

	/**
	 * Generates a map with the original map's values as keys and its keys as values.
	 *
	 * @param map the original map
	 * @return the inverted map
	 * @throws IllegalArgumentException if there are two keys with the same value in the passed map
	 */
	public static <V, K> Map<V, K> inverse(Map<K, V> map) {
		Map<V, K> newMap = new HashMap<>(map.size());
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (newMap.containsKey(entry.getValue())) {
				throw new IllegalArgumentException(String.format(
						"There are two keys with the value %s in the map", entry.getValue()));
			}
			newMap.put(entry.getValue(), entry.getKey());
		}
		return newMap;
	}
}
