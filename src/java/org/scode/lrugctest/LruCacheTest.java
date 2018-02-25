package org.scode.lrugctest;

import org.junit.Test;
import org.scode.lrugctest.LruCache;

import java.util.Optional;

import static org.junit.Assert.*;

public class LruCacheTest {
    @Test
    public void testEviction() {
        final LruCache<String,String> c = new LruCache<>(2);
        assertEquals(0, c.size());

        c.put("k1", "v1");
        assertEquals(1, c.size());

        c.put("k2", "v2");
        assertEquals(2, c.size());

        c.get("k1"); // cause k2 to be evicted prior to k1

        c.put("k3", "v3");
        assertEquals(2, c.size()); // size is still 2

        assertEquals(c.get("k1"), Optional.of("v1"));
    }
}
