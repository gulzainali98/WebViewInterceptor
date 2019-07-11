package com.app.webveiwinterceptor.Model;

import java.util.LinkedList;
import java.util.Queue;

public class CacheRequestModel {

    public Queue<String> cacheURLs;

    private static CacheRequestModel object;

    public CacheRequestModel() {
        cacheURLs = new LinkedList<String>();
    }

    public static CacheRequestModel getCacheRequests() {
        if (object == null) {
            object = new CacheRequestModel();
        }
        return object;
    }


}
