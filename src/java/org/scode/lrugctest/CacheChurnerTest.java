package org.scode.lrugctest;

import org.junit.Test;

import static org.junit.Assert.*;

public class CacheChurnerTest {
    @Test
    public void testHitRatio() {
        CacheChurner c = new CacheChurner(0, 10, (float)0.5);

        // Execute enough iterations to reasonably expect us to land within 0.1% of target
        // hit rate.
        for (int i = 0; i * CacheChurner.CHURN_ITERATIONS < 10000; i++) {
                c.churn();
        }

        float hitRatio = (float)c.hits() / (c.hits() + c.misses());
        assertTrue(hitRatio > 0.49);
        assertTrue(hitRatio < 0.51);
    }
}
