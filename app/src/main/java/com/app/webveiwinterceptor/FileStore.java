package com.app.webveiwinterceptor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.app.webveiwinterceptor.Model.LocalStorageIndex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileStore {

    Activity context;
    public FileStore(Activity context){
        this.context=context;
    }
    public void saveImage(Bitmap bitmapImage, String path){
        // Create imageDir
        File mypath=new File(context.getFilesDir(),path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public Bitmap loadImage(String path)
    {
        Bitmap b= null;
        try {
            File f=new File(context.getFilesDir(), path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public String readHTML(String path){

        String filename=path;
        String data="";
        try {
            //Attaching BufferedReader to the FileInputStream by the help of InputStreamReader
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput(filename)));
            String inputString;
            //Reading data line by line and storing it into the stringbuffer
            while ((inputString = inputReader.readLine()) != null) {

                data=data+inputString;


            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return data;


    }

    public void saveHTML(String HTML, String path){
        String filename=path;
        String data=HTML;

//        Turning map into a string
        File dir = context.getFilesDir();
        File file = new File(dir, path);
        try {
            if (!file.exists()) {

                file.createNewFile();
            }
        }catch (IOException e){}

        FileOutputStream fos;
        try {

            FileOutputStream fOut= new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));



            bw.write(data);
            bw.newLine();


            bw.close();
            fOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();}
        catch (IOException e) {
            e.printStackTrace();}

    }

    public void saveIndex(Map<String, String> index){
        String filename=Constants.INDEX_FILE_NAME;
        ArrayList<String> data=mapToString(index);

//        Turning map into a string
        File dir = context.getFilesDir();
        File file = new File(dir, Constants.INDEX_FILE_NAME);
        try {
            if (!file.exists()) {

                file.createNewFile();
            }
        }catch (IOException e){}


        try {

            FileOutputStream fOut= new FileOutputStream(file,true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            for (int i = 0; i <data.size(); i++) {

                bw.write(data.get(i));
                bw.newLine();
            }

            bw.close();
            fOut.close();

        } catch (FileNotFoundException e) {
        e.printStackTrace();}
        catch (IOException e) {
        e.printStackTrace();}

    }




    private ArrayList<String> mapToString(Map mp) {
        ArrayList<String> data=new ArrayList<>();
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String entry=pair.getKey().toString()+Constants.SKIP_CHARACTER_INDEX_FILE+pair.getValue().toString();
            data.add(entry);


            it.remove(); // avoids a ConcurrentModificationException
        }

        return data;
    }

    public HashMap<String, String> readIndex(){
        HashMap<String,String> index= new HashMap<>();

        String filename=Constants.INDEX_FILE_NAME;
        try {
            //Attaching BufferedReader to the FileInputStream by the help of InputStreamReader
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(
                    context.openFileInput(filename)));
            String inputString;
            //Reading data line by line and storing it into the stringbuffer
            while ((inputString = inputReader.readLine()) != null) {

                String[] keyValuePair= inputString.split(Constants.SKIP_CHARACTER_INDEX_FILE);

                index.put(keyValuePair[0],keyValuePair[1]);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        //Displaying data on the toast
       return index;

    }

    public boolean deleteIndex(){
        File dir = context.getFilesDir();
        File file = new File(dir, Constants.INDEX_FILE_NAME);
        boolean deleted = file.delete();
        return deleted;
    }

    public void clearCache(){
        File dir= context.getFilesDir();

        for(String key : LocalStorageIndex.getObject().index.keySet()){
            File file= new File(dir, LocalStorageIndex.getObject().index.get(key));
            file.delete();
        }
        File file= new File(context.getFilesDir(), Constants.INDEX_FILE_NAME);
        file.delete();
    }

    public void updateIndex(){
        deleteIndex();
//        Log.e("updated index",LocalStorageIndex.getObject().index.toString());
        saveIndex(new HashMap<String, String>(LocalStorageIndex.getObject().index));
    }
}
