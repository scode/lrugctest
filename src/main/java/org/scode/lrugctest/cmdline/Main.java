package org.scode.lrugctest.cmdline;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.scode.lrugctest.CacheChurner;
import org.scode.lrugctest.HiccupDetector;
import org.scode.lrugctest.RateLimiter;

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

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        startHiccupDetector(ns.getInt("hiccup_threshold_nanos"));

        final int threads = ns.getInt("threads");
        final int sizePerCache = ns.getInt("size") / threads;
        final long ratePerThread = ns.getInt("rate") / (long)threads;
        final long burstPerThread = ratePerThread / 250; // 250 ms worth of burst
        final double hitRate = ns.getDouble("hit_rate");
        for (int i = 0; i < ns.getInt("threads"); i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new CacheChurner(
                                threadId,
                                sizePerCache,
                                (float)hitRate,
                                new RateLimiter(ratePerThread, burstPerThread)
                        ).run();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
            }).start();
        }
    }

    private static final void startHiccupDetector(long thresholdNanos) {
        final HiccupDetector hd = new HiccupDetector(thresholdNanos);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hd.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).start();
    }
}
