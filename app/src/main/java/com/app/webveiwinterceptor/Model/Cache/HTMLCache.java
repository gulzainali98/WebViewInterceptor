package com.app.webveiwinterceptor.Model.Cache;

import java.util.HashMap;
import java.util.Map;

public class HTMLCache {


    public Map<String, String> url_html;

    private static HTMLCache object;

    public HTMLCache(){

        url_html= new HashMap<String, String>();
    }

    public static HTMLCache getCache(){
        if(object==null){
            object= new HTMLCache();
        }
        return object;
    }


}
