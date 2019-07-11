package com.app.webveiwinterceptor.Model;

import android.app.Activity;
import android.widget.TextView;

public class CacheManagerModel {

    public TextView mStatusText;
    public Activity context;

    public CacheManagerModel() {
        this.mStatusText = null;
        context = null;
    }

    private static CacheManagerModel object;

    public static CacheManagerModel getObject() {
        if (object == null) {
            object = new CacheManagerModel();
        }
        return object;
    }


}
