package com.app.webveiwinterceptor.HTTPRequests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.app.webveiwinterceptor.Model.Cache.BitmapCache;
import com.app.webveiwinterceptor.Constants;
import com.app.webveiwinterceptor.Model.BitmapModel;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;

import java.net.URL;
import java.net.URLConnection;

public class GetBitmapResource extends AsyncTask<String, Void, BitmapModel> {

    OnTaskCompleted taskCompleted;

    public GetBitmapResource(){}

    public GetBitmapResource(OnTaskCompleted taskCompleted){
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
        if(taskCompleted!=null){
        taskCompleted.onTaskCompleted(Constants.GET_BITMAP_RESOURCE_REQ_ID);}

        super.onPostExecute(resource);
    }
}
