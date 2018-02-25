package org.scode.lrugctest;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class LruGcTest {
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

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final int threads = ns.getInt("threads");
        final int sizePerCache = ns.getInt("size") / threads;
        final long ratePerThread = ns.getInt("rate") / (long)threads;
        final long burstPerThread = ratePerThread / 250; // 250 ms worth of burst
        for (int i = 0; i < ns.getInt("threads"); i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO: hit ratio should be cmdline option
                        new CacheChurner(
                                threadId,
                                sizePerCache,
                                (float)0.50,
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
}
