package org.example.customcollections;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A Map which requires String as keys, is immutable
 * after construction and optimised for reads only.
 * First POC - a binary tree approach: fairly simple, not optimised at all.
 * @param <T> the types of values being supported.
 */
public final class BinaySearchStringMap<T> implements Dictionary<T> {

	private final String[] keys;
	private final T[] values;

	private final int initialMidIdx;
	private final int initialHighIdx;

	public BinaySearchStringMap(String[] keys, T[] values) {
		this.keys = keys;
		this.values = values;
		this.initialHighIdx = keys.length - 1;
		this.initialMidIdx = initialHighIdx >>> 1;
	}

	public T get(String key) {
		int low = 0;
		int high = initialHighIdx;
		int midIndex = initialMidIdx;

		do {
			String midVal = this.keys[midIndex];
			int cmp = midVal.compareTo(key);

			if (cmp < 0)
				low = midIndex + 1;
			else if (cmp > 0)
				high = midIndex - 1;
			else
				return values[midIndex];
			midIndex = (low + high) >>> 1;
 		} while (low <= high);
		return null;//no match
	}

	/**
	 * This builder is not threadsafe.
	 * @param <T>
	 */
	public static class Builder<T> {
		private Map<String,T> mappings = new TreeMap<>();
		public Builder<T> addMapping(String key, T value) {
			Objects.requireNonNull( key );
			Objects.requireNonNull( value );
			checkValid();
			T previous = mappings.put( key, value );
			if (previous!=null) {
				throw new IllegalStateException("Duplicate key detected");
			}
			return this;
		}

		private void checkValid() {
			if (this.mappings ==null) {
				throw new IllegalStateException("This builder has been consumed already: can't build twice or modify its state after build");
			}
		}

		public Builder<T> addMappings(Map<String,T> values) {
			values.forEach( this::addMapping );
			return this;
		}

		public BinaySearchStringMap<T> build() {
			final T[] values = (T[]) new Object[mappings.size()];
			final String[] keys = new String[mappings.size()];
			int i=0;
			for ( Map.Entry<String, T> e : mappings.entrySet() ) {
				keys[i] = e.getKey();
				values[i++] = e.getValue();
			}
			this.mappings = null;
			return new BinaySearchStringMap<>( keys, values );
		}
	}

	public static class NamedObject<T> implements Comparable<String> {
		final T value;
		final String key;

		public NamedObject(String key, T value) {
			this.value = value;
			this.key = key;
		}

		@Override
		public int compareTo(String key) {
			return this.key.compareTo(key);
		}
	}
}
