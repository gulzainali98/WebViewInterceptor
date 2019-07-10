package com.app.webveiwinterceptor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.webveiwinterceptor.Model.Cache.BitmapCache;
import com.app.webveiwinterceptor.Model.Cache.HTMLCache;
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
//        Log.e("Request Method", String.valueOf(request.getMethod()));

        if(request.getUrl().toString().contains("favicon.ico")){
            return super.shouldInterceptRequest(view, request);
        }

        if(!CacheStatus.getStatusObject().collectCache){
            return super.shouldInterceptRequest(view, request);
        }

        if(request.getUrl().toString().contains(".html")){
            //find html cache
//            Log.e("HTML","Exists");


            if(HTMLCache.getCache().url_html.get(request.getUrl().toString())!=null){
//                Log.e("HTML","retrieval");
                Log.e("Browser","Loading HTML Cache");
                InputStream stream = new ByteArrayInputStream(HTMLCache.getCache().url_html.get(request.getUrl().toString()).getBytes(StandardCharsets.UTF_8));
                return new WebResourceResponse("text/html","utf-8",stream);
            }

            else{


                //start the caching process and return the control to the parent.
//                Log.e("Saving Cache Request",request.getUrl().toString());
                CacheRequestModel.getCacheRequests().cacheURLs.add(request.getUrl().toString());
                return super.shouldInterceptRequest(view, request);
            }
        }
        if(request.getUrl().toString().contains(".png")){
//            Log.e("bitmap","exists");
            if(BitmapCache.getCache().url_bitmap.get(request.getUrl().toString())!=null){
//                Log.e("bitmap","retrieval");
                Log.e("Browser","Loading Bitmap Cache");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                BitmapCache.getCache().url_bitmap.get(request.getUrl().toString()).compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream is = new ByteArrayInputStream(bitmapdata);
                return new WebResourceResponse("image/png",null,is);

            }
            else{
//                Log.e("Saving Cache Request",request.getUrl().toString());
                CacheRequestModel.getCacheRequests().cacheURLs.add(request.getUrl().toString());

//
                return super.shouldInterceptRequest(view, request);

            }
        }
//        initiate new caching process

        return super.shouldInterceptRequest(view, request);
    }



}