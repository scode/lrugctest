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

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final int sizePerCache = ns.getInt("size") / ns.getInt("threads");
        for (int i = 0; i < ns.getInt("threads"); i++) {
            final int threadId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO: hit ratio should be cmdline option
                        new CacheChurner(threadId, sizePerCache, (float)0.50).run();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
            }).start();
        }
    }
}
