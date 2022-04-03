package td;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class Utils {

    private Utils() {}

    @Nonnull
    public static <T> Optional<T> getOptional(@Nonnull final List<T> src,
            @Nonnegative final int index) {
        if (index >= src.size()) {
            return Optional.empty();
        }
        T result = src.get(index);
        return Optional.ofNullable(result);
    }
    
    @Nonnull
    public static <T> Optional<T> getOptional(@Nonnull final T[] src,
            @Nonnegative final int index) {
        if (index >= src.length) {
            return Optional.empty();
        }
        T result = src[index];
        return Optional.ofNullable(result);
    }


    public static void checkArgument(final boolean expr) throws IllegalArgumentException {
        if (expr == false) {
            throw new IllegalArgumentException();
        }
    }

    public static int castToInt(final long value) {
        final int result = (int) value;
        if (result != value) {
            throw new ArithmeticException("Out of range: " + value);
        }
        return result;
    }

    @Nonnull
    public static int[] insert(@Nonnull final int[] vals, final int idx, final int val) {
        final int[] newVals = new int[vals.length + 1];
        if (idx > 0) {
            System.arraycopy(vals, 0, newVals, 0, idx);
        }
        newVals[idx] = val;
        if (idx < vals.length) {
            System.arraycopy(vals, idx, newVals, idx + 1, vals.length - idx);
        }
        return newVals;
    }

    public static long getParetoRandom(@Nonnull final Random rnd, final float p, final long max) {
        if (p <= 0 || p >= 1.0) {
            throw new IllegalArgumentException();
        }
        double a = Math.log(1.0 - p) / Math.log(p);
        double x = rnd.nextDouble();
        double y = (Math.pow(x, a) + 1.0 - Math.pow(1.0 - x, 1.0 / a)) / 2.0;
        return max - (long) (max * y);
    }

    @Nonnull
    public static <T> T[] arraySet(@Nonnull T[] src, final int index, final T value) {
        if (index >= src.length) {
            src = Arrays.copyOf(src, src.length * 2);
        }
        src[index] = value;
        return src;
    }
}
