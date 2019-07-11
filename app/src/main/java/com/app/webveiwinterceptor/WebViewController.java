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


import com.app.webveiwinterceptor.HTTPRequests.GetCacheFile;
import com.app.webveiwinterceptor.Model.Cache.CacheInfo;
import com.app.webveiwinterceptor.Model.Cache.CacheMap;
import com.app.webveiwinterceptor.HTTPRequests.GetCacheList;
import com.app.webveiwinterceptor.Interfaces.CacheRequestListener;
import com.app.webveiwinterceptor.Interfaces.CacheStateChangeListener;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;
import com.app.webveiwinterceptor.Model.CacheRequestModel;
import com.app.webveiwinterceptor.Model.CacheStatus;
import com.app.webveiwinterceptor.Model.LocalStorageIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewController extends AppCompatActivity implements CacheRequestListener, OnTaskCompleted, CacheStateChangeListener, View.OnClickListener {

    WebView browser;
    String[] permissions= {Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.INTERNET,
    Manifest.permission.READ_EXTERNAL_STORAGE};


    FileStore store;
    private static final int PERMISSIONS_REQUEST_CODE= 1240;

//    Views
    public TextView mStatusView;
    Button mInitButton;
    Button mNoCacheButton;
    Button mClearCacheButton;
    Switch mCacheSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_controller2);



        store= new FileStore(this);



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




        mStatusView.setText("Please Wait! Initializing Browser and Loading INIT Cache");

        LaunchSequence();



    }

    private void LaunchSequence(){
        loadLocalCache();
        getResourcesToCacheAndStartUI();
    }

    public OnTaskCompleted onInitCacheCompleteListener= new OnTaskCompleted() {
        @Override
        public void onTaskCompleted(int reqID) {
            mStatusView.setText("Initialization Complete!");
            initBroswer(Constants.INIT_URL_BROWSER);
            initUI();
            startCacheService();
        }
    };

    private void getResourcesToCacheAndStartUI(){

        GetCacheList cache= new GetCacheList(this,this);
        cache.execute(Constants.CACHE_URL);
    }

    private void loadLocalCache(){
        FileStore store= new FileStore(this);
        LocalStorageIndex.getObject().index=store.readIndex();
        Set<String> indexKeys=LocalStorageIndex.getObject().index.keySet();
        HashMap<String,String> index= LocalStorageIndex.getObject().index;

        Log.e("Index", index.toString());

        //this if statement is only to print logs so please forgive the complexity of condition statemenet here
        //i am going to delete it afterwards
        if(index.get("https://mockapi1.herokuapp.com/index1.html")!=null && store.readHTML(index.get("https://mockapi1.herokuapp.com/index1.html"))!=null &&
                        store.readHTML(index.get("https://mockapi1.herokuapp.com/index1.html"))!="") {
            Log.e("HTML 4m LoadLocalCache", store.readHTML(index.get("https://mockapi1.herokuapp.com/index1.html").toString()));
        }

        for(String key: indexKeys){
            String urlOfResource=key;

            byte[] data= store.readIntoBytes(index.get(key));

            boolean isFileCached=CacheMap.getMap().map.containsKey(key);
            if(data==null){
                Log.e("Couldn't read", key);
            }
            if(data!=null && !isFileCached){
                Log.e("Local Cache ",key);
                CacheMap.getMap().map.put(key, data);
            }
        }

    }
    private void initBroswer(String initURL){

        browser = (WebView) findViewById(R.id.web_view);
        browser.setWebViewClient(new Browser(this,this));


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

//
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
            for(int i=0; i< CacheInfo.getCacheInfo().resourceCache.size();i++){

                    GetCacheFile cacheFileRequest= new GetCacheFile(this);

                    String url= CacheInfo.getCacheInfo().resourceCache.get(i);

                    if(i==CacheInfo.getCacheInfo().resourceCache.size()-1){
                        cacheFileRequest.setListener(onInitCacheCompleteListener);
                    }
                    cacheFileRequest.execute(url);
                }
        }

    }

    public void initUI(){


        mInitButton.setOnClickListener(this);
        mNoCacheButton.setOnClickListener(this);
        mClearCacheButton.setOnClickListener(this);

    }

    @Override
    public void onStateChanged(String description) {
        mStatusView.setText(description);

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.init_link){
            browser.loadUrl(Constants.INIT_URL_BROWSER);
        }
        if(view.getId()==R.id.no_cache_link){


            browser.loadUrl(Constants.NO_CACHE_URL_BROWSER);
        }
        if(view.getId()==R.id.clear_cache_btn){
            clearRAMCache();
            store.clearCache();


        }

    }

    private void clearRAMCache(){
        browser.clearCache(true);
        CacheInfo.getCacheInfo().resourceCache= new ArrayList<>();
        LocalStorageIndex.getObject().index= new HashMap<>();

        CacheMap.getMap().map=new HashMap<>();

    }






    @Override
    public void cacheRequest(int reqID, String URL) {

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
            mStatusView.setText("Caching Resources");
            GetCacheFile cachReq= new GetCacheFile(new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(int reqID) {
                    mStatusView.setText("Resource Cached");
                }
            }, this);

            cachReq.execute(URL);
        }
    }
}
