package main;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2,jvmArgs = {"-Xms4G","-Xmx4G"})
public class BenchmarkTest {

    private static final long N = 10_000_000L;

    @Benchmark
    public  long sequentialSum(){
        return Stream.iterate(1L,i->i+1).limit(N).reduce(0L,Long::sum);
    }

    @Benchmark
    public long iterativeSum(){
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long rangedSum(){
        return LongStream.rangeClosed(1,N).reduce(0L,Long::sum);
    }

    @Benchmark
    public long parallerangedSum(){
        return LongStream.rangeClosed(1,N).parallel().reduce(0L,Long::sum);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
