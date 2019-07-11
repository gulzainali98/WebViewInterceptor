package com.app.webveiwinterceptor.HTTPRequests;

import android.app.Activity;
import android.os.AsyncTask;

import com.app.webveiwinterceptor.Constants;
import com.app.webveiwinterceptor.FileStore;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;
import com.app.webveiwinterceptor.Model.Cache.CacheMap;
import com.app.webveiwinterceptor.Model.HTTPReqModel;
import com.app.webveiwinterceptor.Model.LocalStorageIndex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetCacheFile extends AsyncTask<String, Void, HTTPReqModel> {

    OnTaskCompleted taskCompleted;
    Activity context;

    FileStore store;

    public void setListener(OnTaskCompleted taskCompleted){
        this.taskCompleted=taskCompleted;
    }

    public GetCacheFile(Activity context){
        this.context=context;
        taskCompleted=null;
        store= new FileStore(context);
    }

    public GetCacheFile(OnTaskCompleted taskCompleted, Activity context){
        store= new FileStore(context);
        this.context=context;
        this.taskCompleted=taskCompleted;
    }


    @Override
    protected HTTPReqModel doInBackground(String... params) {
        String requestUrl = params[0];

        byte[] data= null;

        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            int lenghtOfFile = conn.getContentLength();

            data= readBytes(conn.getInputStream());
//            CacheHolder.getObject().data=data;

        } catch (Exception ex) {
        }
        HTTPReqModel resource= new HTTPReqModel();
        resource.data=data;
        resource.url=requestUrl;

        return resource;
    }

    @Override
    protected void onPostExecute(HTTPReqModel resource) {



        //updating RAM Cache

        CacheMap.getMap().map.put(resource.url,resource.data);

        String pathOfFile=resource.url.replace("/","-");

        store.saveFile(resource.data,pathOfFile);

        LocalStorageIndex.getObject().index.put(resource.url,pathOfFile);


        store.updateIndex();



        if(taskCompleted!=null){
            taskCompleted.onTaskCompleted(Constants.GET_CACHE_FILE_REQ_ID);}

        super.onPostExecute(resource);
    }


    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


}
