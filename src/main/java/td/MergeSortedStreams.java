package td;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nonnull;

public class MergeSortedStreams implements SortedIntegerStream {

    private final SortedIntegerStream x, y;

    // Feel free to add more instance fields.
    private Integer left, right;

    public MergeSortedStreams(SortedIntegerStream x, SortedIntegerStream y) {
        // You can assume |x| and |y| are sorted, but need to assume these streams can be EXTREMELY LONG.        
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean hasNext() {
        // TODO implement this
        return false;
    }

    @Override
    public int getNext() throws IndexOutOfBoundsException {
        // TODO implement this
        return 0;
    }

}


interface SortedIntegerStream {
    /**
     * Returns true if the stream has a remaining value.
     *
     * <p>
     * It does not consume the stream.
     */
    boolean hasNext();

    /**
     * Returns the next value in the stream.
     *
     * <p>
     * It consumes one integer value in the stream.
     *
     * <p>
     * It throws {@code IndexOutOfBoundsException} when the stream is empty.
     */
    int getNext() throws IndexOutOfBoundsException;
}


class IntArrayStream implements SortedIntegerStream {

    @Nonnull
    private final Queue<Integer> queue;

    IntArrayStream(@Nonnull Integer... integers) {
        this.queue = new LinkedList<>();
        for (Integer i : integers) {
            queue.offer(i);
        }
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public int getNext() throws IndexOutOfBoundsException {
        if (queue.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return queue.poll();
    }

}
