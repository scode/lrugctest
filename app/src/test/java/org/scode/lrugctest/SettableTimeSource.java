package org.scode.lrugctest;

class SettableTimeSource implements ITimeSource {
    public long nanoTime = 0;

    @Override
    public long nanoTime() {
        return this.nanoTime;
    }
}
