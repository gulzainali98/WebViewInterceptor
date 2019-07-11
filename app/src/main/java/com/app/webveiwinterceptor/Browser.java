package com.app.webveiwinterceptor;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.webveiwinterceptor.Model.Cache.CacheMap;
import com.app.webveiwinterceptor.Interfaces.CacheRequestListener;
import com.app.webveiwinterceptor.Model.CacheRequestModel;
import com.app.webveiwinterceptor.Model.CacheStatus;

import java.io.ByteArrayInputStream;

public class Browser extends WebViewClient {

    public Browser() {
    }

    CacheRequestListener listener;
    Activity context;

    public Browser(CacheRequestListener listener, Activity context) {
        this.listener = listener;
        this.context = context;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {

        System.out.print(request.getRequestHeaders());
        Log.e("getting url", String.valueOf(request.getUrl()));
        String url = request.getUrl().toString();


        if (url.contains("favicon.ico")) {
            return super.shouldInterceptRequest(view, request);
        }

        if (!CacheStatus.getStatusObject().collectCache) {
            return super.shouldInterceptRequest(view, request);
        }
        if (isFileCached(url)) {
            Log.e("Serving", "m'lord");
            ByteArrayInputStream inputStream=getCachedFileInputStream(url);
            return new WebResourceResponse(null, null, inputStream);
        } else {
            addToCache(url);
            return super.shouldInterceptRequest(view, request);
        }
    }

    private void addToCache(String url) {

        Log.e("Browser", "Request to Cache");
        CacheRequestModel.getCacheRequests().cacheURLs.add(url);
        CacheManager manager = new CacheManager();
        manager.handleCache(url);


    }

    private ByteArrayInputStream getCachedFileInputStream(String url) {
        byte byteFileArray[] = CacheMap.getMap().map.get(url);
        ByteArrayInputStream is = new ByteArrayInputStream(byteFileArray);

        return is;
    }

    private boolean isFileCached(String url) {
        return CacheMap.getMap().map.containsKey(url);
    }


}