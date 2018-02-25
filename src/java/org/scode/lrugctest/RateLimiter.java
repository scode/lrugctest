package org.scode.lrugctest;

/**
 * Rate-limiter intended for thread-local rate limitation.
 */
public class RateLimiter {
    public interface ITimeSource {
        long nanoTime();
    }

    public static class SystemTimeSource implements ITimeSource {
        @Override
        public long nanoTime() {
            return System.nanoTime();
        }
    }

    private final long perSecond;
    private final long nanosPerHit;
    private final long maxBurst;
    private final ITimeSource timeSource;

    private long tokens = 0;
    private long lastRefillNanos = 0;

    public RateLimiter(long perSecond, long maxBurst, ITimeSource timeSource) {
        this.perSecond = perSecond;
        this.nanosPerHit = 1000000000 / (perSecond);
        this.maxBurst = maxBurst;
        this.timeSource = timeSource;
        this.tokens = maxBurst;
        this.lastRefillNanos = timeSource.nanoTime();
    }

    /**
     * @return 0 if ok to proceed, >0 to request sleep of said many nano seconds before
     *         retrying.
     */
    public long tryNext() {
        if (this.tokens > 0) {
            --this.tokens;
            return 0;
        }  else {
            long now = this.timeSource.nanoTime();
            long elapsed = now - this.lastRefillNanos;

            if (elapsed >= this.nanosPerHit) {
                this.tokens = elapsed / this.nanosPerHit;
                this.lastRefillNanos = this.lastRefillNanos + (this.tokens * nanosPerHit);
                this.tokens = Math.min(this.tokens, this.maxBurst - 1);
                return 0;
            } else {
                return nanosPerHit - elapsed;
            }
        }
    }

    /**
     * Wait however long is necessary to allow another operation.
     *
     * @return The number of nano seconds that we had to wait.
     */
    public long waitForNext() {
        long amountSlept = 0;
        while (true) {
            long sleepNeeded = this.tryNext();
            if (sleepNeeded == 0) {
                return amountSlept;
            }

            amountSlept += sleepNeeded;
            long millis = sleepNeeded / 1000000L;
            try {
                Thread.sleep(millis, (int) (sleepNeeded - millis));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
