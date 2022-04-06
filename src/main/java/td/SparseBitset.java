package td;

import static td.Utils.arraySet;
import static td.Utils.castToInt;
import static td.Utils.checkArgument;
import static td.Utils.getOptional;
import static td.Utils.insert;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.LongConsumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class SparseBitset {
    private static final Logger logger = Logger.getLogger(SparseBitset.class.getName());

    private static final int DEFAULT_CONTAINER_SIZE = 1024 * 8;
    private static final float CONVERSION_THRESHOLD_PERC = 0.3f;

    private final int containerSize;
    private final int conversionThreshold;
    @Nonnull
    private Container[] containers;

    public SparseBitset() {
        this(DEFAULT_CONTAINER_SIZE);
    }

    public SparseBitset(final int containerSize) {
        checkArgument(containerSize >= 1);

        this.containerSize = containerSize;
        this.conversionThreshold = Math.round(containerSize * CONVERSION_THRESHOLD_PERC);
        this.containers = new Container[1000];
    }

    public boolean get(final long index) {
        checkArgument(index >= 0);

        final int partition = castToInt(index / containerSize);
        if (partition <= containers.length) {
            return false;
        }

        final Container container = containers[partition];
        if (container == null) {
            return false;
        }

        int pos = castToInt(index % containerSize);
        return container.get(pos);
    }

    public void set(final long index) {
        checkArgument(index >= 0);

        final int partition = castToInt(index / containerSize);
        final int pos = castToInt(index % containerSize);

        final Optional<Container> result = getOptional(containers, partition);
        if (result.isPresent()) {
            Container container = result.get();
            container.set(pos);
            if (container.type() == ContaineType.array && container.size() >= conversionThreshold) {
                logger.info("converded to bitset container at partition: " + partition);
                Container newContainer = new BitsetContainer(container.indicies());
                this.containers = arraySet(containers, partition, newContainer);
            }
        } else {
            Container container = new ArrayContainer(pos);
            this.containers = arraySet(containers, partition, container);
        }
    }

    public void forEach(@Nonnull final LongConsumer consumer) {
        for (int i = 0; i < containers.length; i++) {
            final Container c = containers[i];
            if (c != null) {
                final long baseIndex = i * DEFAULT_CONTAINER_SIZE;
                c.stream().forEach(x -> {
                    consumer.accept(baseIndex + x);
                });
            }
        }
    }

    public long size() {
        long sum = 0L;
        for (Container c : containers) {
            if (c != null) {
                sum += c.size();
            }
        }
        return sum;
    }

    enum ContaineType {
        bitset, array;
    }

    interface Container {

        @Nonnull
        ContaineType type();

        /**
         * @return previous value
         */
        boolean set(int index);

        boolean get(int index);

        int size();

        @Nonnull
        int[] indicies();

        @Nonnull
        IntStream stream();

    }

    static final class BitsetContainer implements Container {

        @Nonnull
        private final BitSet set;

        BitsetContainer(@Nonnull int[] elements) {
            this.set = new BitSet();
            for (int i : elements) {
                set.set(i);
            }
        }

        @Override
        public ContaineType type() {
            return ContaineType.bitset;
        }

        @Override
        public boolean set(final int index) {
            if (set.get(index)) {
                return true;
            } else {
                set.set(index);
                return false;
            }
        }

        @Override
        public boolean get(final int index) {
            return set.get(index);
        }

        @Override
        public int size() {
            return set.cardinality();
        }

        @Override
        public int[] indicies() {
            return set.stream().toArray();
        }

        @Override
        public IntStream stream() {
            return set.stream();
        }

    }

    static final class ArrayContainer implements Container {
        private final int INITIAL_SIZE = 128;

        @Nonnull
        private int[] elements;
        @Nonnegative
        private int size;

        ArrayContainer(@Nonnull int element) {
            this.elements = new int[INITIAL_SIZE];
            elements[0] = element;
            this.size = 1;
        }

        @Override
        public ContaineType type() {
            return ContaineType.array;
        }

        @Override
        public boolean set(final int element) {
            final int index = Arrays.binarySearch(elements, 0, size, element);
            if (index >= 0) {// skip if already set
                return true;
            }
            this.elements = insert(elements, ~index, element);
            this.size += 1;
            return false;
        }

        @Override
        public boolean get(final int element) {
            return Arrays.binarySearch(elements, 0, size, element) < 0;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int[] indicies() {
            return Arrays.copyOf(elements, size);
        }

        @Override
        public IntStream stream() {
            return IntStream.of(indicies());
        }

    }

}
