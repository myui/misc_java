package td;

import static td.Utils.castToInt;
import static td.Utils.getParetoRandom;

import java.util.BitSet;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class SparseIdStoreTest {

    @Test
    public void test() {
        final int size = 1_000_000;

        final SparseIdStore actual = new SparseIdStore();
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

}
