package com.example.tal.myfirstapplication;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gront on 12/03/2016.
 */


public class Requests{
    private static Requests instance;

    private Requests() {
    }

    public static Requests getInstance(){
        if (instance ==null){
            instance = new Requests();
        }
        return instance;
    }

    public String doGet(String urlString){
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = reader.readLine())!=null){
                res.append(line);
            }
            return res.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }



    public String doPost(String urlString, String var){

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            //urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bf.write(var);
            bf.flush();
            bf.close();
            outputStream.close();
            urlConnection.getInputStream();



            return "";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    public String doPost(String urlString, JSONObject jobj) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bf.write(jobj.toString());
            bf.flush();
            bf.close();
            outputStream.close();
            urlConnection.getInputStream();

            return "";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }




    public String doPostWithResponse(String urlString, String var){
        StringBuilder res = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bf.write(var);
            bf.flush();
            bf.close();
            outputStream.close();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine())!=null){
                res.append(line);
            }
            return res.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }



    public String doPostWithResponse(String urlString, JSONObject var){
        StringBuilder res = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bf.write(var.toString());
            bf.flush();
            bf.close();
            outputStream.close();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine())!=null){
                res.append(line);
            }
            return res.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    /*  */

}
