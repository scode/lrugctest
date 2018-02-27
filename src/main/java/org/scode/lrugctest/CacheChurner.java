package org.scode.lrugctest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CacheChurner implements Runnable {
    /**
     * Number of iterations against the cache to perform per churn(). Only purpose is to allow
     * the outer loop to have expensive operations in it without significantly impacting
     * the behavior of the test.
     *
     * Package access for testing.
     */
    static final int CHURN_ITERATIONS = 1000;

    private final int id;
    private final float targetHitRatio;
    private final RateLimiter rateLimiter;

    private final int size;
    private final LruCache<Integer,String> cache;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final Random random;

    private volatile boolean stopRequested = false;

    private long hits;
    private long misses;

    /**
     * @param id An identifier like a thread id; intended for logging.
     * @param size Size of the LRU cache in number of items.
     * @param targetHitRatio The target hit ratio to achieve in the cache.
     */
    public CacheChurner(int id, int size, float targetHitRatio, RateLimiter rateLimiter) {
        this.id = id;
        this.size = size;
        this.targetHitRatio = targetHitRatio;
        this.rateLimiter = rateLimiter;
        this.cache = new LruCache<>(size);

        // Deterministic seed is on purpose for testing purposes.
        this.random = new Random(id);
    }

    /**
     * Request that the churner stops as soon as possible. May be called from any thread.
     */
    public void stop() {
        this.stopRequested = true;
    }

    public boolean isStopRequested() {
        return this.stopRequested;
    }

    /** NOT thread-safe. For tests. */
    long hits() {
        return this.hits;
    }

    /** NOT thread-safe. For tests.*/
    long misses() {
        return this.misses;
    }

    @Override
    public void run() {
        try {
            boolean haveReportedFull = false;

            while (!this.stopRequested) {
                churn();

                if (!haveReportedFull && this.cache.size() == this.size) {
                    System.err.println("" + this.id + ": cache now full");
                    haveReportedFull = true;
                }
            }

            System.err.println("" + this.id + ": stop requested, exiting");
        } finally {
            this.countDownLatch.countDown();
        }
    }

    /**
     * Execute one iteration of churn. Package level access for testing purposes.
     */
    void churn() {
        for (int i = 0; i < CHURN_ITERATIONS; i++) {
            this.rateLimiter.waitForNext();

            // Use [0, targetHitRatio] for keys, thus causing us to hit our target hit ratio.
            int key = this.random.nextInt((int) (this.size / this.targetHitRatio));

            if (this.cache.get(key).isPresent()) {
                ++this.hits;
            } else {
                ++this.misses;
                this.cache.put(key, "value");
            }
        }
    }
}
