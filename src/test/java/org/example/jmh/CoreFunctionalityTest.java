package org.example.jmh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.example.customcollections.BinaySearchStringMap;
import org.example.customcollections.Dictionary;

public class CoreFunctionalityTest {

	@Test
	public void basic() {
		Dictionary<SomeType> map = new BinaySearchStringMap.Builder<SomeType>()
				.addMapping( "k1", v("k1") )
				.addMapping( "k2", v("k2") ).build();
		Assertions.assertEquals( "k1", map.get( "k1" ).v );
		Assertions.assertEquals( "k2", map.get( "k2" ).v );
		Assertions.assertNull( map.get( "k3" ) );
	}

	private SomeType v(String value) {
		return new SomeType( value );
	}

	static class SomeType {
		private final String v;

		SomeType(String v) {
			this.v = v;
		}
	}
}
