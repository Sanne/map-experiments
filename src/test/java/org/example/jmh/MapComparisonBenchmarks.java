package org.example.jmh;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.example.customcollections.StringMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
//@Fork(1)
public class MapComparisonBenchmarks {

	private StringMap<String> stringMap;
	private Map<String, String> hashMap;
	private Map<String, String> concurrentMap;

	@Setup
	public void init() {
		hashMap = new HashMap<>();
		for (int i=0; i<50; i++ ) {
			//Adding 50 more entries so the maps aren't too empty,
			//but ensure they all have the same copies for a fair
			//comparison.
			hashMap.put( randomString(), randomString() );
		}
		hashMap.put( "initialKey", "Pimmi" );
		hashMap.put( "Pimmi", "Penguin" );
		hashMap.put( "Penguin", "ok" );
		stringMap = new StringMap.Builder<String>()
				.addMappings( hashMap )
				.build();
		concurrentMap = new ConcurrentHashMap<>( hashMap );
	}

	private static String randomString() {
		return UUID.randomUUID().toString();
	}

	@Benchmark
	public void getFromHashMap(final Blackhole bh) {
		String k1 = "initialKey";
		String k2 = hashMap.get(k1);
		String k3 = hashMap.get(k2);
		String k4 = hashMap.get(k3);
		if (!"ok".equals( k4 )) {
			throw new IllegalStateException();
		}
		bh.consume( k4 );
	}

	@Benchmark
	public void getFromStringMap(final Blackhole bh) {
		String k1 = "initialKey";
		String k2 = stringMap.get(k1);
		String k3 = stringMap.get(k2);
		String k4 = stringMap.get(k3);
		if (!"ok".equals( k4 )) {
			throw new IllegalStateException();
		}
		bh.consume( k4 );
	}

	@Benchmark
	public void getFromCHMap(final Blackhole bh) {
		String k1 = "initialKey";
		String k2 = concurrentMap.get(k1);
		String k3 = concurrentMap.get(k2);
		String k4 = concurrentMap.get(k3);
		if (!"ok".equals( k4 )) {
			throw new IllegalStateException();
		}
		bh.consume( k4 );
	}

	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()
				.include( MapComparisonBenchmarks.class.getSimpleName())
//				.jvmArgs("-XX:+UnlockDiagnosticVMOptions",
//						 "-XX:+DebugNonSafepoints",
//						 "-XX:+PreserveFramePointer")
//				.addProfiler( LinuxPerfAsmProfiler.class)
				.build();

		new Runner( opt).run();
	}

}
