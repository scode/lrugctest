package org.scode.lrugctest;

public class SystemTimeSource implements ITimeSource {
    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
