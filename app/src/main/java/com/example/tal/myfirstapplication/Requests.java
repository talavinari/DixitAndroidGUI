package com.example.tal.myfirstapplication;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Requests{

    public static String doGet(String urlString){
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.disconnect();
            System.setProperty("http.keepAlive", "false");
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
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

    public static String doPost(String urlString, JSONObject jobj) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("connection", "close");
            System.setProperty("http.keepAlive", "false");
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




    public static String doPostWithResponse(String urlString, String var){
        StringBuilder res = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("connection", "close");
            System.setProperty("http.keepAlive", "false");
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
//
//    private static boolean checkPlayServices(Context context) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(context, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i(Constants.TAG_MAIN_CLASS, "This device is not supported.");
////                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public static String doPostWithResponse(String urlString, JSONObject var){
        StringBuilder res = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("connection", "close");
            System.setProperty("http.keepAlive", "false");
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
}
