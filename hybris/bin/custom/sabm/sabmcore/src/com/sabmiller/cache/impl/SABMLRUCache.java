package com.sabmiller.cache.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SABMLRUCache<K, V> extends LinkedHashMap<K, V> {

    private final int cacheSize;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public SABMLRUCache(final int cacheSize) {
        super(cacheSize, DEFAULT_LOAD_FACTOR, true);
        this.cacheSize = cacheSize;
    }

    /**
     * Gets the value from the map if {@link #containsKey(Object#key)} returns true regardless of the value if null or not, otherwise
     * it retrieves from the loader then put it in the map
     * @param key
     * @param loader
     * @return
     */
    public V getValueOrLoad(final K key, Function<K,V> loader){
        Objects.requireNonNull(loader,"loader is required.");

        if(containsKey(key)){
            return get(key);
        }

        final V loadedValue = loader.apply(key);
        put(key,loadedValue);
        return loadedValue;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.cacheSize < this.size();
    }
}
