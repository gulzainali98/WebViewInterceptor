package com.app.webveiwinterceptor.HTTPRequests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.app.webveiwinterceptor.Model.Cache.CacheInfo;
import com.app.webveiwinterceptor.Constants;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

public class GetCacheList extends AsyncTask<String, Void, JSONObject> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;


    OnTaskCompleted taskCompleted;
    Activity context;



    public GetCacheList(){}

    public GetCacheList(OnTaskCompleted taskCompleted, Activity context){
        this.taskCompleted=taskCompleted;
        this.context=context;

    }



    @Override
    protected JSONObject doInBackground(String... params){
        String stringUrl = params[0];
        String result= new String();


        String inputLine;


        try {

            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();
            connection.connect();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);


            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

        }
        catch(IOException e){
            Log.e("Exception", e.getMessage());
        }
        JSONParser parser = new JSONParser();
        JSONObject json= new JSONObject();
        try {
            json = (JSONObject) parser.parse(result);
        }
        catch(ParseException e){

        }

        return json;
    }
    @Override
    protected void onPostExecute(JSONObject result){
        Log.e("Result",result.toString());

        ArrayList<String> cacheResource= new ArrayList<>();
        Set<String> keys= result.keySet();

//        Iterator<String> keys = result.keySet();

            for(String key : keys){


                    // do something with jsonObject here
                    cacheResource.add(String.valueOf(result.get(key)));

            }
        CacheInfo.getCacheInfo().resourceCache=cacheResource;
            if(taskCompleted!=null){
        taskCompleted.onTaskCompleted(Constants.GET_CACHE_REQ_ID);}


        super.onPostExecute(result);
    }
}