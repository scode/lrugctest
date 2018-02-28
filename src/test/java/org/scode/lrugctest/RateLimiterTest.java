package org.scode.lrugctest;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class RateLimiterTest {
    private static class TimeSource implements ITimeSource {
        public long nanoTime = 0;

        @Override
        public long nanoTime() {
            return this.nanoTime;
        }
    }

    /**
     * A time soure which returns a fixed sequence of times.
     */
    private static class ArrayTimeSource implements ITimeSource {
        private final long[] nanoTimes;
        private int i = 0;

        public ArrayTimeSource(long[] nanoTimes) {
            this.nanoTimes = nanoTimes;
        }

        @Override
        public long nanoTime() {
            if (this.i >= this.nanoTimes.length) {
                throw new RuntimeException("ran out of times");
            }

            return this.nanoTimes[this.i++];
        }
    }

    @Test
    public void testMaxBurst() {
        TimeSource ts = new TimeSource();
        RateLimiter rl = new RateLimiter(1000L, 100L, ts);

        // On creation, we should have one full burst available to us.
        for (int i = 0; i < 100; i++) {
            assertEquals(0, rl.tryNext());
        }

        assertEquals(1000000L, rl.tryNext());

        // Jump forward a full second. We should have exactly one full burst available to us,
        // not one second's worth.
        ts.nanoTime += 1000000000L;

        for (int i = 0; i < 100; i++) {
            assertEquals(0, rl.tryNext());
        }

        assertEquals(1000000L, rl.tryNext());
    }

    @Test
    public void testWaitForNext() {
        // Rely very specifically on exactly when the time source is called.
        ITimeSource ts = new ArrayTimeSource(new long[] {
                0L,           // Initial call in constructor.
                0L,           // First call after running out.
                1000000L,     // Call after sleeping.
        });
        RateLimiter rl = new RateLimiter(1000L, 1L, ts);

        // Consume burst buffer.
        assertEquals(0, rl.waitForNext());

        long startTime = System.nanoTime();
        // Block for 1 ms.
        assertEquals(1000000, rl.waitForNext());
        long stopTime = System.nanoTime();

        // The primary purpose of this test is to ensure we don't throw an exception,
        // and that waitForNext() communicates that it slept. In order to properly test
        // the sleeping behavior we should inject a mockable sleeper interface. But for now,
        // this assertion doesn't hurt (it won't fail falsely) - but it cannot be relied upon
        // (because it will succeed falsely).
        assertTrue((stopTime - startTime) > 1000000);
    }
}
