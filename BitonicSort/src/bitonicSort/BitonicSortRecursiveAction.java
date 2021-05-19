package bitonicSort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class BitonicSortRecursiveAction<T> extends RecursiveAction {
    private final T[] list;
    private final int offset, count;
    private final int parallelism;
    private final Comparator<T> comp;
    private final boolean dir;

    public BitonicSortRecursiveAction(T[] list, int offset, int count, int parallelism, Comparator<T> comp, boolean dir) {
        this.list = list;
        this.offset = offset;
        this.count = count;
        this.parallelism = parallelism;
        this.comp = comp;
        this.dir = dir;
    }

    protected void compute() {
        int MIN_SORT_GRAN = 1 << 13;

        if (count < MIN_SORT_GRAN) {
            Arrays.sort(list, offset, offset + count, dir ? comp : comp.reversed());
            return;
        }

        int subCount = count / 2;
        int subParallelism = parallelism <= 1 ? parallelism : parallelism / 2;

        List<BitonicSortRecursiveAction<T>> subTasks = Arrays.asList(
                new BitonicSortRecursiveAction<>(list, offset, subCount,
                        subParallelism, comp, !dir),
                new BitonicSortRecursiveAction<>(list, offset + subCount, count - subCount,
                        subParallelism, comp, dir)
        );

        if (parallelism <= 1)
            subTasks.forEach(BitonicSortRecursiveAction::compute);
        else
            invokeAll(subTasks);

        BitonicSort.mergeRecursiveSerial(list, offset, count, comp, dir);
    }
}
