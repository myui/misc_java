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
            if (container.type() == ContaineType.bitset && container.size() >= conversionThreshold) {
                logger.info("converded to array container at partition: " + partition);
                Container newContainer = new ArrayContainer(container.indicies());
                this.containers = arraySet(containers, partition, newContainer);
            }
        } else {
            Container container = new BitsetContainer();
            container.set(pos);
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
        private final BitSet elements;

        BitsetContainer() {
            this.elements = new BitSet();
        }

        @Override
        public ContaineType type() {
            return ContaineType.bitset;
        }

        @Override
        public boolean set(final int index) {
            if (elements.get(index)) {
                return true;
            } else {
                elements.set(index);
                return false;
            }
        }

        @Override
        public boolean get(final int index) {
            return elements.get(index);
        }

        @Override
        public int size() {
            return elements.cardinality();
        }

        @Override
        public int[] indicies() {
            return elements.stream().toArray();
        }

        @Override
        public IntStream stream() {
            return elements.stream();
        }

    }

    static final class ArrayContainer implements Container {

        @Nonnull
        private int[] elements;

        ArrayContainer(@Nonnull int[] elememts) {
            this.elements = elememts;
        }

        @Override
        public ContaineType type() {
            return ContaineType.array;
        }

        @Override
        public boolean set(final int element) {
            final int index = Arrays.binarySearch(elements, element);
            if (index >= 0) {// skip if already set
                return true;
            }
            this.elements = insert(elements, ~index, element);
            return false;
        }

        @Override
        public boolean get(final int element) {
            return Arrays.binarySearch(elements, element) < 0;
        }

        @Override
        public int size() {
            return elements.length;
        }

        @Override
        public int[] indicies() {
            return elements;
        }

        @Override
        public IntStream stream() {
            return IntStream.of(elements);
        }

    }

}
