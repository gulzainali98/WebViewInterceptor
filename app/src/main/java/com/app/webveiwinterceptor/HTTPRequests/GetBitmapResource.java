package com.app.webveiwinterceptor.HTTPRequests;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.app.webveiwinterceptor.FileStore;
import com.app.webveiwinterceptor.Model.Cache.BitmapCache;
import com.app.webveiwinterceptor.Constants;
import com.app.webveiwinterceptor.Model.BitmapModel;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;
import com.app.webveiwinterceptor.Model.LocalStorageIndex;

import java.net.URL;
import java.net.URLConnection;

public class GetBitmapResource extends AsyncTask<String, Void, BitmapModel> {

    OnTaskCompleted taskCompleted;
    Activity context;

    public GetBitmapResource(){}

    public GetBitmapResource(OnTaskCompleted taskCompleted, Activity context){
        this.context=context;
        this.taskCompleted=taskCompleted;
    }

    @Override
    protected BitmapModel doInBackground(String... params) {
        String requestUrl = params[0];
        Bitmap bitmap= null;

        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
        }
        BitmapModel resource= new BitmapModel();
        resource.bitmap=bitmap;
        resource.URL=requestUrl;
        return resource;
    }

    @Override
    protected void onPostExecute(BitmapModel resource) {

        BitmapCache.getCache().url_bitmap.put(resource.URL,resource.bitmap);

        FileStore store= new FileStore(context);

        String pathOfBitmap=resource.URL.replace("/","-");
        store.saveImage(resource.bitmap,pathOfBitmap);

        LocalStorageIndex.getObject().index.put(resource.URL,pathOfBitmap);
        store.updateIndex();

        if(taskCompleted!=null){
        taskCompleted.onTaskCompleted(Constants.GET_BITMAP_RESOURCE_REQ_ID);}

        super.onPostExecute(resource);
    }
}
