package com.nekit508.updater;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.util.serialization.JsonValue;

public class JsonLoadingCache {
    private ObjectMap<Fi, JsonValue> cache = new ObjectMap<>();

    public void put(Fi fi, JsonValue jsonValue) {
        cache.put(fi, jsonValue);
    }

    public JsonValue get(Fi fi) {
        return cache.get(fi);
    }

    public boolean contains(Fi fi) {
        return cache.containsKey(fi);
    }
}
