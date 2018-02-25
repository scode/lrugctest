package org.scode.lrugctest;

import net.sourceforge.argparse4j.ArgumentParsers;

public class LruGcTest {
    public static void main(String[] args) {
        ArgumentParsers.newFor("LruGcTest").build()
                .defaultHelp(true)
                .description("Exercise the garbage collector with an LRU cache workload.");
    }
}
