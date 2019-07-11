package com.app.webveiwinterceptor.Model.Cache;


import java.util.HashMap;

public class CacheMap {

    public HashMap<String, byte[]> map;

    private static CacheMap object;

    public CacheMap() {
        map = new HashMap<>();
    }

    public static CacheMap getMap() {
        if (object == null) {

            object = new CacheMap();
        }
        return object;
    }
}
