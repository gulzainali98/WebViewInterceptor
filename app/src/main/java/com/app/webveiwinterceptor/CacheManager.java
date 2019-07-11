package com.app.webveiwinterceptor;

import android.app.Activity;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android.os.Handler;
import android.widget.TextView;

import com.app.webveiwinterceptor.HTTPRequests.GetCacheFile;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;
import com.app.webveiwinterceptor.Model.CacheManagerModel;

public class CacheManager {

    Handler handler;

    public CacheManager() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Log.e("Handler Called", (String) message.obj);
                String url = (String) message.obj;
                buildCache(url);

            }
        };
    }

    public void handleCache(String Url) {
        Message message = handler.obtainMessage(1, Url);
        message.sendToTarget();
    }

    private void buildCache(String url) {
        String URL = url;
        Activity context = CacheManagerModel.getObject().context;
        TextView status = CacheManagerModel.getObject().mStatusText;
        status.setText("Caching Resources");
        GetCacheFile cachReq = new GetCacheFile(new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(int reqID) {
                TextView status = CacheManagerModel.getObject().mStatusText;
                status.setText("Resource Cached");
            }
        }, context);

        cachReq.execute(URL);

    }
}
