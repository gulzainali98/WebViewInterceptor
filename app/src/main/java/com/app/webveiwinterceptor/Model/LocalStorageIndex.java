package com.app.webveiwinterceptor.Model;

import java.util.HashMap;

public class LocalStorageIndex {

    public HashMap<String,String> index;

    private static LocalStorageIndex object;

    public LocalStorageIndex(){
        index= new HashMap<String,String>();
    }

    public static LocalStorageIndex getObject(){
        if(object==null){
            object= new LocalStorageIndex();
        }
        return object;
    }


}
