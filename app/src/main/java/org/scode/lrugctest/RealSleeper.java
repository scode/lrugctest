package org.scode.lrugctest;

public class RealSleeper implements ISleeper {
    @Override
    public void sleep(long millis, int nanos) throws InterruptedException {
        Thread.sleep(millis, nanos);
    }
}
