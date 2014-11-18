package de.unistuttgart.iste.rss.bugminer.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	private MapUtils() { }

	public static <V, K> Map<V, K> inverse(Map<K, V> map) {
		Map<V, K> newMap = new HashMap<>(map.size());
		for (Map.Entry<K, V> entry : map.entrySet()) {
			newMap.put(entry.getValue(), entry.getKey());
		}
		return newMap;
	}
}
