package com.app.webveiwinterceptor.Model.Cache;

import java.util.ArrayList;
import java.util.List;

public class CacheInfo {

    public List<String> resourceCache;

    private static CacheInfo object;

    public CacheInfo() {
        resourceCache = new ArrayList<String>();
    }

    public static CacheInfo getCacheInfo() {
        if (object == null) {
            object = new CacheInfo();
        }
        return object;
    }

}
