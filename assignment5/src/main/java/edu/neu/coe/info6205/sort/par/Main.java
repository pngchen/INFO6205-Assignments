package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
//        ParSort.threadCount = 16;
        int len = 4000000;
        System.out.println("Degree of Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        Random random = new Random();
        int[] array = new int[len];
        ArrayList<Long> timeList = new ArrayList<>();

        // Warm Up
        ParSort.cutoff = 22 * len / 100;
        ParSort.threadCount = 16;
        // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
        long time;
        long startTime = System.currentTimeMillis();
        for (int t = 0; t < 10; t++) {
            for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
            ParSort.sort(array, 0, array.length);
        }
        long endTime = System.currentTimeMillis();
        time = (endTime - startTime);
        timeList.add(time);


        System.out.println("Warm Up");
        System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t10times Time:" + time + "ms");

        for (int j = 1; j <= 64; j = j * 2) {
            // To find a good cutoff value
//            ParSort.cutoff = 22 * len / 100;
//            ParSort.threadCount = j;

            // To find a good thread value
            ParSort.cutoff = 22 * len / 100;
            ParSort.threadCount = j;

            // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
//            long time;
            startTime = System.currentTimeMillis();
            for (int t = 0; t < 10; t++) {
                for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                ParSort.sort(array, 0, array.length);
            }
            endTime = System.currentTimeMillis();
            time = (endTime - startTime);
            timeList.add(time);


            System.out.println("Num of Thread: " + ParSort.threadCount);
            System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t10times Time:" + time + "ms");

        }
        try {
//            FileOutputStream fis = new FileOutputStream("./src/result" + (ParSort.threadCount) +".csv");
            FileOutputStream fis = new FileOutputStream("./src/result" + (len) +"_thread.csv");
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            int j = 0;
            for (long i : timeList) {
//                String content = (double) j / 100 + "," + (double) i / 10 + "\n";   // For cutoff
                String content = (double) j / 100 + "," + (double) i / 10 + "\n";   // For threads
                if (j == 0) {
                    j = 1;
                } else {
                    j = j * 2;
                }
                bw.write(content);
                bw.flush();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
