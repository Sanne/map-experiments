package org.example.customcollections;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

/**
 * A Map which requires String as keys, is immutable
 * after construction and optimised for reads only.
 * First POC - a binary tree approach: fairly simple, not optimised at all.
 * @param <T> the types of values being supported.
 */
public final class StringMap<T> {

	private final NamedObject<T>[] values;

	private StringMap(NamedObject<T>[] values) {
		this.values = values;
	}

	public T get(String key) {
		int r = Arrays.binarySearch( this.values, key, null );
		if (r>=0) {
			return values[r].value;
		}
		else {
			return null;
		}
	}

	/**
	 * This builder is not threadsafe.
	 * @param <T>
	 */
	public static class Builder<T> {
		private Map<String,T> mappings = new TreeMap<>();
		Builder<T> addMapping(String key, T value) {
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

		public StringMap<T> build() {
			final IntFunction<NamedObject<T>[]> arraybuilder = NamedObject[]::new;
			final NamedObject<T>[] values = arraybuilder.apply( mappings.size() );
			final BiFunction<String,T, NamedObject<T>> namedObjectBuilder = NamedObject::new;
			int i=0;
			for ( Map.Entry<String, T> e : mappings.entrySet() ) {
				values[i++] = namedObjectBuilder.apply( e.getKey(), e.getValue() );
			}
			this.mappings = null;
			return new StringMap<>( values );
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
