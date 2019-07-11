package com.app.webveiwinterceptor;

import android.app.Activity;
import android.graphics.Bitmap;
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
import com.app.webveiwinterceptor.Model.LocalStorageIndex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Browser extends WebViewClient {

    public Browser(){}

    CacheRequestListener listener;
    Activity context;
    public Browser(CacheRequestListener listener, Activity context){
        this.listener=listener;
        this.context=context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }



    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, final WebResourceRequest request) {

        System.out.print(request.getRequestHeaders());
        Log.e("getting url", String.valueOf(request.getUrl()));



        if(request.getUrl().toString().contains("favicon.ico")){
            return super.shouldInterceptRequest(view, request);
        }

        if(!CacheStatus.getStatusObject().collectCache){
            return super.shouldInterceptRequest(view, request);
        }

        boolean isFileCached= CacheMap.getMap().map.containsKey(request.getUrl().toString());
        if(isFileCached){
            byte byteFileArray[]= CacheMap.getMap().map.get(request.getUrl().toString());
            ByteArrayInputStream isnew = new ByteArrayInputStream(byteFileArray);
            return new WebResourceResponse(null, null, isnew);
        }
        else{

            Log.e("Browser", "Request to Cache");
            CacheRequestModel.getCacheRequests().cacheURLs.add(request.getUrl().toString());
            return super.shouldInterceptRequest(view, request);
        }
    }



}