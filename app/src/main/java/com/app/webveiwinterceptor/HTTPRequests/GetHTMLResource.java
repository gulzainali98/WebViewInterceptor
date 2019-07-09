package com.app.webveiwinterceptor.HTTPRequests;

import android.os.AsyncTask;
import android.util.Log;

import com.app.webveiwinterceptor.Constants;
import com.app.webveiwinterceptor.Model.Cache.HTMLCache;
import com.app.webveiwinterceptor.Model.HTMLModel;
import com.app.webveiwinterceptor.Interfaces.OnTaskCompleted;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetHTMLResource extends AsyncTask<String, Void, HTMLModel> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    OnTaskCompleted taskCompleted;


    public GetHTMLResource(){}

    public GetHTMLResource(OnTaskCompleted taskCompleted){
        this.taskCompleted=taskCompleted;
    }




    @Override
    protected HTMLModel doInBackground(String... params){
        String stringUrl = params[0];
        String result= new String();

        HTMLModel model= new HTMLModel();

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
        model.HTML=result;
        model.URL=stringUrl;
        return model;
    }
    @Override
    protected void onPostExecute(HTMLModel result){
        Log.e("Result",result.HTML);
        HTMLCache.getCache().url_html.put(result.URL,result.HTML);
        if(taskCompleted!=null){
        taskCompleted.onTaskCompleted(Constants.GET_HTML_RESOURCE_REQ_ID);}
        super.onPostExecute(result);
    }


}