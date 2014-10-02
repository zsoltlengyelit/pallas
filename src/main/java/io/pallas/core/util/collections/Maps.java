package io.pallas.core.util.collections;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author lzsolt
 *
 */
public class Maps {

	/**
	 * Flips values and key of map.
	 *
	 * @param map
	 *            map within flip values and key
	 * @param target
	 *            target map
	 */
	public static <K, V> void flip(final Map<K, V> map, final Map<V, K> target) {

		for (final Entry<K, V> entry : map.entrySet()) {
			target.put(entry.getValue(), entry.getKey());
		}

	}
}
