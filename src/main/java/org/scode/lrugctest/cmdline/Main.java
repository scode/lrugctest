package org.scode.lrugctest.cmdline;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.scode.lrugctest.CacheChurner;
import org.scode.lrugctest.HiccupDetector;
import org.scode.lrugctest.RateLimiter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("LruGcTest").build()
                .defaultHelp(true)
                .description("Exercise the garbage collector with an LRU cache workload.");
        parser.addArgument("-s", "--size")
                .type(Integer.class)
                .setDefault(10000)
                .help("Total size of LRU cache (number of items)");
        parser.addArgument("-t", "--threads")
                .type(Integer.class)
                .setDefault(1)
                .help("Number of concurrent threads.");
        parser.addArgument("-r", "--rate")
                .type(Integer.class)
                .setDefault(1000000)
                .help("Number of cache checks per second.");
        parser.addArgument("--hiccup-threshold-nanos")
                .type(Integer.class)
                .setDefault(1000000)
                .help("Minimum length of a detected hiccup that will be reported.");
        parser.addArgument("--hit-rate")
                .type(Double.class) // should be Float.class, but triggers casting failure in getFloat()
                .setDefault(0.5)
                .help("Hit rate. Valid values are in range [0.0,1.0].");
        parser.addArgument("--time-limit-seconds")
                .type(Integer.class)
                .setDefault(0)
                .help("Stop after this many seconds. 0 keeps running indefinitely.");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final int threads = ns.getInt("threads");
        final int timeLimitSeconds = ns.getInt("time_limit_seconds");
        if (timeLimitSeconds < 0) {
            System.err.println("--time-limit-seconds must be >= 0");
            System.exit(1);
        }

        final HiccupDetector hiccupDetector = new HiccupDetector(ns.getInt("hiccup_threshold_nanos"));
        final Thread hiccupThread = startHiccupDetector(hiccupDetector);

        final List<CacheChurner> churners = new ArrayList<>(threads);
        final int sizePerCache = ns.getInt("size") / threads;
        final long ratePerThread = ns.getInt("rate") / (long)threads;
        final long burstPerThread = ratePerThread / 250; // 250 ms worth of burst
        final double hitRate = ns.getDouble("hit_rate");
        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            final CacheChurner churner = new CacheChurner(
                    threadId,
                    sizePerCache,
                    (float)hitRate,
                    new RateLimiter(ratePerThread, burstPerThread)
            );
            churners.add(churner);
            Thread worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        churner.run();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
            }, "cache-churner-" + threadId);
            worker.start();
        }

        if (timeLimitSeconds > 0) {
            startTimeLimitWatcher(timeLimitSeconds, churners, hiccupDetector, hiccupThread);
        }
    }

    private static Thread startHiccupDetector(final HiccupDetector hd) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hd.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }, "hiccup-detector");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private static void startTimeLimitWatcher(
            final int timeLimitSeconds,
            final List<CacheChurner> churners,
            final HiccupDetector hiccupDetector,
            final Thread hiccupThread) {
        Thread stopper = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(timeLimitSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                System.err.println("Time limit reached (" + timeLimitSeconds + "s); stopping workload.");

                for (CacheChurner churner : churners) {
                    churner.stop();
                }

                for (CacheChurner churner : churners) {
                    try {
                        churner.awaitStop();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                hiccupDetector.stop();
                try {
                    hiccupThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.exit(0);
            }
        }, "time-limit");

        stopper.setDaemon(true);
        stopper.start();
    }
}
