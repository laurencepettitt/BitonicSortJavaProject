package bitonicSort;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ForkJoinPool;

/**
 * Sorts a sequence of values of type T using the bitonic sort algorithm.
 * Comparator class may be given as a parameter
 * or, if the class supports it, the default comparator may be used instead.
 */
public class BitonicSort {

    /**
     * Swap value in list at position i with value in list at position j
     * where (0 <= i,j <= list.length) and (i != j).
     *
     * @param i Position in list
     * @param j Position in list
     */
    private static <T> void exchange(T[] list, int i, int j) {
        T t = list[i];
        list[i] = list[j];
        list[j] = t;
    }

    /**
     * Compare value in list at position i with value at position j in direction dir.
     *
     * @param i   Position in list
     * @param j   Position in list
     * @param dir Direction of comparison, i.e. boolean value representing either 'up' or 'down'
     */
    private static <T> void compareAndExchange(T[] list, int i, int j, final Comparator<T> comp, boolean dir) {
        if (comp.compare(list[i], list[j]) > 0 == dir)
            exchange(list, i, j);
    }

    /**
     * Finds largest integer which is a power of two and less than n.
     *
     * @param n Integer for which the return value will be the largest power of two less than.
     * @return Largest integer which is a power of two and less than n.
     */
    protected static int maxPow2(int n) {
        int k = 1;
        while (k > 0 && k < n)
            k = k << 1;
        return k >>> 1;
    }

    /**
     * Merges a bitonic sequence, of length (count/2), starting at offset,
     * into a sorted sequence of length (count/2)
     *
     * @param offset Offset from beginning of list
     * @param count  Length of sequence merge will be performed on
     * @param dir    Direction of comparison
     */
    public static <T> void mergeRecursiveSerial(T[] list, int offset, int count, Comparator<T> comp, boolean dir) {
        if (count <= 1) return;

        int n = maxPow2(count);

        for (int i = offset; i < offset + count - n; ++i) {
            compareAndExchange(list, i, i + n, comp, dir);
        }

        mergeRecursiveSerial(list, offset, n, comp, dir);
        mergeRecursiveSerial(list, offset + n, count - n, comp, dir);
    }

    /**
     * Recursive (serial) function to sort sequence, of length count, starting at offset, in direction dir.
     *
     * @param list   List to be sorted
     * @param offset Offset from beginning of list
     * @param count  Length of sequence sort will be performed on
     * @param dir    Direction of comparison
     */
    private static <T> void sortRecursiveSerialUtil(T[] list, int offset, int count, Comparator<T> comp, boolean dir) {
        if (count <= 1) return;

        int split = count / 2;

        sortRecursiveSerialUtil(list, offset, split, comp, !dir);
        sortRecursiveSerialUtil(list, offset + split, count - split, comp, dir);

        mergeRecursiveSerial(list, offset, count, comp, dir);
    }

    /**
     * Recursive (serial) function to sort sequence, of length count, using Comparator comp.
     *
     * @param list List to be sorted
     * @param comp Comparator to define natural ordering
     */
    public static <T> void sortRecursiveSerial(T[] list, Comparator<T> comp) {
        sortRecursiveSerialUtil(list, 0, list.length, comp, true);
    }

    /**
     * Recursive function to sort sequence, using comparator comp.
     *
     * @param comp Comparator to define ordering
     */
    public static <T> void sortRecursiveAction(T[] list, Comparator<T> comp) {
        final int length = list.length;


        if (length == 0 || length == 1) return;

        int PARALLELISM = 8;
        ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
        BitonicSortRecursiveAction<T> bs = new BitonicSortRecursiveAction<>(list, 0, length, PARALLELISM, comp, true);
        pool.invoke(bs);
    }

    /**
     * Sorts given array of type T using given comparator.
     * When the function finishes, given array will be sorted.
     *
     * @param list Array to be sorted
     * @param comp Comparator class to use for comparisons
     */
    public static <T> void sort(T[] list, Comparator<? super T> comp) {
        assert comp != null;
        sortRecursiveAction(list, comp);
    }

    /**
     * Sorts given array of type T using default comparator in natural order.
     * Type T must be extend Comparable.
     * When the function finishes, given array will be sorted.
     *
     * @param list Array to be sorted
     */
    public static <T extends Comparable<? super T>> void sort(T[] list) {
        sort(list, Comparator.naturalOrder());
    }


    /**
     * Sorts given list of type T using given comparator.
     * When the function finishes, given list will be sorted.
     *
     * @param list List to be sorted
     * @param c    Comparator class to use for comparisons
     */
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        assert c != null;
        Object[] a = list.toArray();
        sort(a, (Comparator) c);
        ListIterator<T> i = list.listIterator();
        for (Object t : a) {
            i.next();
            i.set((T) t);
        }
    }

    /**
     * Sorts given list of type T using default comparator in natural order.
     * Type T must be extend Comparable.
     * When the function finishes, given list will be sorted.
     *
     * @param list List to be sorted
     */
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        sort(list, Comparator.naturalOrder());
    }
}
