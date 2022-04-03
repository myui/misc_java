package td;

import static td.Utils.castToInt;
import static td.Utils.getParetoRandom;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.LongStream;

import org.junit.Assert;
import org.junit.Test;

public class SparseBitsetTest {

    @Test
    public void testPareto() {
        final int size = 1_000_000;

        final SparseBitset actual = new SparseBitset();
        final BitSet expected = new BitSet(size);

        final Random rnd = new Random(43);
        for (int i = 0; i < size; i++) {
            long v = getParetoRandom(rnd, 0.8f, size);
            actual.set(v);
            expected.set(castToInt(v));
        }

        Assert.assertEquals(expected.cardinality(), actual.size());
        actual.forEach(x -> {
            Assert.assertTrue(expected.get(castToInt(x)));
        });
    }

    @Test
    public void testPrintPareto() {
        final int size = 10_000;
        final Random rnd = new Random(43);
        final long[] data = new long[size];
        for (int i = 0; i < size; i++) {
            long v = getParetoRandom(rnd, 0.8f, size);
            data[i] = v;
        }
        Arrays.sort(data);
        //LongStream.of(data).forEach(System.out::println);
    }

}
