package org.scode.lrugctest;

public class MockSleeper implements ISleeper {
    public long nanosSleptSoFar = 0;

    @Override
    public void sleep(long millis, int nanos) throws InterruptedException {
        this.nanosSleptSoFar += millis * 1000000;
        this.nanosSleptSoFar += nanos;
    }
}
