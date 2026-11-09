package org.scode.lrugctest;

public class HiccupDetector implements Runnable {
    private final long thresholdNanos;
    private final ITimeSource timeSource;
    private final ISleeper sleeper;
    private volatile boolean stopRequested = false;

    private static final long ONE_MS = 1000000;

    public HiccupDetector(long thresholdNanos) {
        this(thresholdNanos, new SystemTimeSource(), new RealSleeper());
    }

    public HiccupDetector(long thresholdNanos, ITimeSource timeSource, ISleeper sleeper) {
        this.timeSource = timeSource;
        this.sleeper = sleeper;
        this.thresholdNanos = thresholdNanos;
    }

    @Override
    public void run() {
        while (!this.stopRequested) {
            oneIteration();
        }
    }

    public void stop() {
        this.stopRequested = true;
    }

    /**
     * Package acccess for testing.
     *
     * @returns The number of nanos overrun.
     **/
    long oneIteration() {
        try {
            long startTime = this.timeSource.nanoTime();
            this.sleeper.sleep(1, 0);
            long stopTime = this.timeSource.nanoTime();

            long elapsed = stopTime - startTime;

            if (elapsed > ONE_MS + this.thresholdNanos) {
                long overrun = elapsed - ONE_MS;
                System.err.println("HICCUP: " + overrun + "ns (" + overrun / ONE_MS + "ms)");
                return overrun;
            }
            return 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
