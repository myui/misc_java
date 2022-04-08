package td;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        if (left != null || right != null) {
            return true;
        }
        if (x.hasNext() || y.hasNext()) {
            return true;
        }
        return false;
    }

    @Override
    public int getNext() throws IndexOutOfBoundsException {
        if (left == null && x.hasNext()) {
            this.left = x.getNext();
        }
        if (right == null && y.hasNext()) {
            this.right = y.getNext();
        }

        final int ret;
        if (left != null) {
            if (right != null && right.intValue() <= left.intValue()) {
                ret = right.intValue();
                this.right = null;
            } else {
                ret = left.intValue();
                this.left = null;
            }
        } else if (right != null) {
            assert (left == null);
            ret = right.intValue();
            this.right = null;
        } else {
            throw new IndexOutOfBoundsException();
        }

        return ret;
    }
    
    public static void main(String[] args) {
        SortedIntegerStream stream1 = new IntArrayStream(1,3,5,6,6,9);
        SortedIntegerStream stream2 = new IntArrayStream(3,4,5,5,7,8,11);
        SortedIntegerStream merged = new MergeSortedStreams(stream1, stream2);
        
        List<Integer> result = new ArrayList<>();
        while(merged.hasNext()) {
            result.add(merged.getNext());            
        }
        System.out.println("expected: [1, 3, 3, 4, 5, 5, 5, 6, 6, 7, 8, 9, 11]");
        System.out.println("actual  : " + result);
        
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
