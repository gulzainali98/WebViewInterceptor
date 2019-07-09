package com.app.webveiwinterceptor.Model.Cache;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class BitmapCache {

    public Map<String, Bitmap> url_bitmap;



    private static BitmapCache object;

    public BitmapCache(){
        url_bitmap=new HashMap<String, Bitmap>();
    }

    public static BitmapCache getCache(){

        if(object==null){

            object = new BitmapCache();
        }
        return object;
    }
}
