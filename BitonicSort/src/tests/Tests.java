package tests;

import bitonicSort.BitonicSort;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

interface ITest {
    boolean test();
}

/**
 * Provides a number of methods which run tests against the BitonicSort class.
 * Tests include integration tests and performance tests.
 */
public class Tests {

    /**
     * Checks whether given list is sorted according to comparator comp
     *
     * @param list List to check if sorted
     * @param comp Comparator to define order
     * @param <T>  Type of elements of list
     * @return Whether list is sorted
     */
    private static <T> boolean isSorted(List<T> list, Comparator<T> comp) {
        if (list == null || list.size() <= 1) return true;
        for (int i = 1; i < list.size(); i++)
            if (comp.compare(list.get(i - 1), list.get(i)) > 0)
                return false;
        return true;
    }

    /**
     * Sorts given list using BitonicSort class and returns true if BitonicSort sorted list successfully
     *
     * @param list List to sort
     * @param comp Comparator to define order
     * @param <T>  Type of elements of list
     * @return Whether list was sorted successfully by BitonicSort
     */
    private static <T> boolean sortsList(List<T> list, Comparator<T> comp) {
        BitonicSort.sort(list, comp);
        try {
            return isSorted(list, comp);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Test
     *
     * @return Whether sorts integer list of length which is not a power of two.
     */
    private static boolean testSortsIntegerList() {
        List<Integer> list = new ArrayList<>();
        list.add(65);
        list.add(23);
        list.add(89);
        list.add(1);
        list.add(555555555);
        try {
            return sortsList(list, Comparator.naturalOrder());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test
     *
     * @return Whether sorts random integer list of length 10.
     */
    private static boolean testSortsRandomIntegerList() {
        List<Integer> list = new Random().ints(10, 0, 200).boxed().collect(Collectors.toList());
        try {
            return sortsList(list, Comparator.naturalOrder());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test
     *
     * @return Whether sorts random integer list of length 10, in reverse order.
     */
    private static boolean testSortsReverseRandomIntegerList() {
        List<Integer> list = new Random().ints(8, Integer.MIN_VALUE, Integer.MAX_VALUE).boxed().collect(Collectors.toList());
        return sortsList(list, Comparator.reverseOrder());
    }

    /**
     * Test
     * Performance tests to compare speed of sorting a random integer list
     * with bitonicSort.BitonicSort.sort vs java.util.List.sort
     */
    private static long perf(Runnable test) {
        long startTime = System.nanoTime();
        test.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static void performanceTests() {
        String format = " %-50.50s %-10.10s%n";
        int nanoPerMilli = 1_000_000;

        int length = 1 << 23;
        int unitLength = 1_000_000;
        double unitsPerLength = (double) length / (double) unitLength;

        Integer[] list_orig = new Random(42).ints(length, Integer.MIN_VALUE, Integer.MAX_VALUE).boxed()
                .toArray(Integer[]::new);

        Function<BiConsumer<Integer[], Comparator<Integer>>, Runnable> makeTestRunner = (test) -> {
            Integer[] list_copy = Arrays.copyOf(list_orig, list_orig.length);
            return () -> test.accept(list_copy, Comparator.naturalOrder());
        };

        Map<String, Runnable> tests = new HashMap<>();
        tests.put("Arrays::sort", makeTestRunner.apply(Arrays::sort));
        tests.put("Arrays::parallelSort", makeTestRunner.apply(Arrays::parallelSort));
        tests.put("BitonicSort::sortRecursiveSerial", makeTestRunner.apply(BitonicSort::sortRecursiveSerial));
        tests.put("BitonicSort::sortRecursiveAction", makeTestRunner.apply(BitonicSort::sortRecursiveAction));

        Map<String, Long> results = tests.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> perf(e.getValue())));

        results.forEach((name, res) -> System.out.printf(format, name, ((double) res / nanoPerMilli) / unitsPerLength));

    }

    /**
     * Run all Test
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        Map<String, ITest> tests = new HashMap<>();
        tests.put("SortsIntegerList", Tests::testSortsIntegerList);
        tests.put("SortsRandomIntegerList", Tests::testSortsRandomIntegerList);
        tests.put("SortsReverseRandomIntegerList", Tests::testSortsReverseRandomIntegerList);

        System.out.println(("Tests: "));
        for (Map.Entry<String, ITest> test :
                tests.entrySet()) {
            System.out.printf(
                    " %-50.50s %-7.7s%n",
                    test.getKey(),
                    (test.getValue().test() ? "success" : "fail")
            );
        }

        System.out.println();

        System.out.println("Performance (milliseconds): ");
        performanceTests();
    }
}
