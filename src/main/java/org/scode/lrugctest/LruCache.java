package org.scode.lrugctest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LruCache<Key, Value> {
    private final int maxSize;
    private final LinkedHashMap<Key, Value> hmap;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        this.hmap = new LinkedHashMap<Key,Value>(16, (float)0.75, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Key,Value> eldest) {
                return size() > LruCache.this.maxSize;
            }
        };
    }

    public void put(Key key, Value value) {
        this.hmap.put(key, value);
    }

    public Optional<Value> get(Key key) {
        return Optional.ofNullable(this.hmap.get(key));
    }

    public int size() {
        return this.hmap.size();
    }
}
