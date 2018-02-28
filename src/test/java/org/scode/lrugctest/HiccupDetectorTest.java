package org.scode.lrugctest;

import org.junit.Test;

import static org.junit.Assert.*;

public class HiccupDetectorTest {
    @Test
    public void testHiccupNotDetected() {
        ArrayTimeSource ts = new ArrayTimeSource(new long[]{
                0L,
                1000005,
        });
        MockSleeper sleeper = new MockSleeper();

        HiccupDetector hd = new HiccupDetector(10, ts, sleeper);
        assertEquals(0, hd.oneIteration());
    }

    @Test
    public void testHiccupDetected() {
        ArrayTimeSource ts = new ArrayTimeSource(new long[]{
                0L,
                1000015,
        });
        MockSleeper sleeper = new MockSleeper();

        HiccupDetector hd = new HiccupDetector(10, ts, sleeper);
        assertEquals(15, hd.oneIteration());
    }
}
