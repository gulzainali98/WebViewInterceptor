package com.app.webveiwinterceptor.Model;

public class CacheStatus {

    public boolean collectCache;

    private static CacheStatus object;

    public CacheStatus(){
        collectCache =true;
    }

    public static CacheStatus getStatusObject(){
        if(object==null){
            object= new CacheStatus();
        }
        return object;
    }

}
