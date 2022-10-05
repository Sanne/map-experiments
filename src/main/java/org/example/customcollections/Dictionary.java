package org.example.customcollections;

import java.util.Map;

public interface Dictionary<T> {

	T get(String key);

	static <T> Dictionary<T> makeFrom(Map<String,T> values) {
		return new BinaySearchStringMap.Builder<T>()
				.addMappings( values )
				.build();
	}

}
