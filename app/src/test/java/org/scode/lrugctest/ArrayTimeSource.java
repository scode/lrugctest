package org.scode.lrugctest;

/**
 * A time soure which returns a fixed sequence of times.
 */
class ArrayTimeSource implements ITimeSource {
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
