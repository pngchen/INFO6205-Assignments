/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.*;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static Integer[] randomArr(int n) {
        Integer[] Arr = new Integer[n];
        Random r = new Random(1);
        for (int i = 0; i < n; i++) {
            Arr[i] = r.nextInt(n);
        }
        return Arr;
    }

    public static Integer[] orderedArr(int n) {
        Integer[] Arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            Arr[i] = i;
        }
        return Arr;
    }

    public static Integer[] partiallyOrderedArr(int n) {
        Integer[] Arr = new Integer[n];
        Random r = new Random(1);
        for (int i = 0; i < n/2; i++) {
            Arr[i] = r.nextInt(n/2);
        }
        for (int i = n/2; i < n; i++) {
            Arr[i] = i;
        }
        return Arr;
    }

    public static Integer[] reverseOrderedArr(int n) {
        Integer[] Arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            Arr[i] = n - 1 - i;
        }
        return Arr;
    }

    public static double sortArr (Integer[] Arr, String description, int len) {
        InsertionSort<Integer> insertionSort = new InsertionSort<>();
        Benchmark<Integer> benchmark = new Benchmark_Timer<>(description + " length = " + len, null,
                num -> {
                    insertionSort.sort(Arr);
                }, null);
        double t = benchmark.run(Arr.length, 100);
        System.out.println(t);
        return t;
    }

    public static void main (String[] args) {
        for (int len = 1; len < 512; len = len * 8) {
            Integer[] randomArr = randomArr(len);
            Integer[] orderedArr = orderedArr(len);
            Integer[] partiallyOrderedArr = partiallyOrderedArr(len);
            Integer[] reverseOrderedArr = reverseOrderedArr(len);
            double randomArrT = sortArr(randomArr, "random", len);
            double orderedArrT = sortArr(orderedArr, "ordered", len);
            double partiallyOrderedArrT = sortArr(partiallyOrderedArr, "partiallyOrdered", len);
            double reverseOrderedArrT = sortArr(reverseOrderedArr, "reverseOrdered", len);
        }

        for (int len = 512; len < 66000; len = len * 2) {
            Integer[] randomArr = randomArr(len);
            Integer[] orderedArr = orderedArr(len);
            Integer[] partiallyOrderedArr = partiallyOrderedArr(len);
            Integer[] reverseOrderedArr = reverseOrderedArr(len);
            double randomArrT = sortArr(randomArr, "random", len);
            double orderedArrT = sortArr(orderedArr, "ordered", len);
            double partiallyOrderedArrT = sortArr(partiallyOrderedArr, "partiallyOrdered", len);
            double reverseOrderedArrT = sortArr(reverseOrderedArr, "reverseOrdered", len);
        }
    }
}
