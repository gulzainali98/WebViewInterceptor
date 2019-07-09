package com.app.webveiwinterceptor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.app.webveiwinterceptor.Model.Cache.BitmapCache;
import com.app.webveiwinterceptor.Model.Cache.CacheInfo;
import com.app.webveiwinterceptor.Model.Cache.HTMLCache;
import com.app.webveiwinterceptor.HTTPRequests.GetBitmapResource;
import com.app.webveiwinterceptor.HTTPRequests.GetCacheList;
import com.app.webveiwinterceptor.HTTPRequests.GetHTMLResource;
import com.app.webveiwinterceptor.Interfaces.CacheRequestListener;
import com.app.webveiwinterceptor.Interfaces.CacheStateChangeListener;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;
import com.app.webveiwinterceptor.Model.CacheRequestModel;
import com.app.webveiwinterceptor.Model.CacheStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewController extends AppCompatActivity implements CacheRequestListener, OnTaskCompleted, CacheStateChangeListener, View.OnClickListener {

    WebView browser;
    String[] permissions= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.INTERNET};
    GetBitmapResource bitmapRequest;
    GetHTMLResource HTMLrequest;
    GetCacheList cache;
    private static final int PERMISSIONS_REQUEST_CODE= 1240;

//    Views
    TextView mStatusView;
    Button mInitButton;
    Button mNoCacheButton;
    Button mClearCacheButton;
    Switch mCacheSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_controller2);

        if(!checkAndRequestPermissions()){
            return;
        }
        mStatusView=(TextView)findViewById(R.id.status);
        mInitButton=(Button)findViewById(R.id.init_link);
        mNoCacheButton=(Button)findViewById(R.id.no_cache_link);
        mClearCacheButton=(Button)findViewById(R.id.clear_cache_btn);
        mCacheSwitch=(Switch)findViewById(R.id.cache_switch);

        mCacheSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                CacheStatus.getStatusObject().collectCache =isChecked;
            }
        });

        mInitButton.setOnClickListener(this);
        mNoCacheButton.setOnClickListener(this);
        mClearCacheButton.setOnClickListener(this);


        bitmapRequest= new GetBitmapResource(this);

        HTMLrequest= new GetHTMLResource(this);
        cache=new GetCacheList(this);
        mStatusView.setText("Initializing Browser and Loading INIT Cache");

        cache.execute(Constants.CACHE_URL);


    }

    private void initBroswer(String initURL){

        browser = (WebView) findViewById(R.id.web_view);
        browser.setWebViewClient(new Browser(this));


        browser.getSettings().setAllowFileAccess(true);
        browser.getSettings().setAllowFileAccessFromFileURLs(true);
        browser.getSettings().setAllowUniversalAccessFromFileURLs(true);
        browser.getSettings().setAllowContentAccess(true);

        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);



        browser.loadUrl(initURL);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(requestCode==PERMISSIONS_REQUEST_CODE){

            for (int i=0; i<grantResults.length;i++){

                if(grantResults[i]== PackageManager.PERMISSION_DENIED){

                    Toast.makeText(this,"You have not granted all permissions!",Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    public boolean checkAndRequestPermissions(){

        List<String> listPermissionsNeeded=new ArrayList<>();
        for(String perm: permissions){

            if(ContextCompat.checkSelfPermission(this,perm)
            != PackageManager.PERMISSION_GRANTED){

                listPermissionsNeeded.add(perm);
            }

        }

        if(!listPermissionsNeeded.isEmpty()){

            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);

        }
        return true;
    }


    @Override
    public void onTaskCompleted(int reqID) {
        if(reqID== Constants.GET_CACHE_REQ_ID){
            Log.e("Printing Cache", CacheInfo.getCacheInfo().resourceCache.toString());
                for(String url : CacheInfo.getCacheInfo().resourceCache){
                    if(url.contains(".html")){
                        HTMLrequest.execute(url);
                        Log.e("Start HTML Cache",url) ;
                    }
                }
        }
        if(reqID==Constants.GET_HTML_RESOURCE_REQ_ID){
            for(String url : CacheInfo.getCacheInfo().resourceCache){
                if(url.contains(".png")){

                    bitmapRequest.execute(url);
                    Log.e("Start Bitmap Cache", url);
                }
            }
        }

        if(reqID==Constants.GET_BITMAP_RESOURCE_REQ_ID){

            mStatusView.setText("Initialization Complete.");
            initBroswer(Constants.INIT_URL_BROWSER);
            startCacheService();
        }
    }

    @Override
    public void onStateChanged(String description) {
        Log.e("State Changed",description);
        mStatusView.setText(description);
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.init_link){
            browser.loadUrl(Constants.INIT_URL_BROWSER);
        }
        if(view.getId()==R.id.no_cache_link){

//            mimicProcess(Constants.GET_HTML_RESOURCE_REQ_ID,Constants.NO_CACHE_URL_BROWSER);
            browser.loadUrl(Constants.NO_CACHE_URL_BROWSER);
        }
        if(view.getId()==R.id.clear_cache_btn){
            clearCache();
            browser.clearCache(true);
        }

    }

    private void clearCache(){
        CacheInfo.getCacheInfo().resourceCache= new ArrayList<>();
        BitmapCache.getCache().url_bitmap=new HashMap<>();
        HTMLCache.getCache().url_html= new HashMap<>();
    }

    public void mimicProcess(int reqID,String URL){
        mStatusView.setText("Cache Started : "+URL);
        if(reqID==Constants.GET_BITMAP_RESOURCE_REQ_ID){
            bitmapRequest= new GetBitmapResource(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(int reqID) {
                    mStatusView.setText("Done");
                }
            });
            bitmapRequest.execute(URL);
        }
        if(reqID==Constants.GET_HTML_RESOURCE_REQ_ID){
            HTMLrequest= new GetHTMLResource(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(int reqID) {
                    mStatusView.setText("Done");
                }
            });
            HTMLrequest.execute(URL);
        }


    }

    @Override
    public void cacheRequest(int reqID, String URL) {
        mStatusView.setText("Cache Started : "+URL);
        if(reqID==Constants.GET_BITMAP_RESOURCE_REQ_ID){
            bitmapRequest= new GetBitmapResource(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(int reqID) {
                    mStatusView.setText("Done");
                }
            });
            bitmapRequest.execute(URL);
        }
        if(reqID==Constants.GET_HTML_RESOURCE_REQ_ID){
            HTMLrequest= new GetHTMLResource(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(int reqID) {
                    mStatusView.setText("Done");
                }
            });
            HTMLrequest.execute(URL);
        }
    }

    public void startCacheService() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            CacheService();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 4000); //execute in every 50000 ms
    }

    public void CacheService(){

        Queue cacheQueue= CacheRequestModel.getCacheRequests().cacheURLs;
        if(cacheQueue.size()==0){
            mStatusView.setText("Everything is cached! :)");
        }
        Iterator<String> it= cacheQueue.iterator();
        for(int i=0; i< cacheQueue.size(); i++){
            String URL= cacheQueue.poll().toString();
            Log.e("Caching service: ", URL);
            if(URL.contains(".html")){
                mStatusView.setText("Caching HTML Resource: "+ URL);
                GetHTMLResource req= new GetHTMLResource(new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(int reqID) {
                        mStatusView.setText("HTML Resource Cached");
                    }
                });
                req.execute(URL);
            }
            if(URL.contains(".png")){
                mStatusView.setText("Caching Bitmap Resource: "+ URL);
                GetBitmapResource req= new GetBitmapResource(new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(int reqID) {
                        mStatusView.setText("Bitmap Resourced Cached");
                    }
                });
                req.execute(URL);
            }
        }
    }
}
