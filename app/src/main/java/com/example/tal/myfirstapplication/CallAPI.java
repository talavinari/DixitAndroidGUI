package com.example.tal.myfirstapplication;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gront on 12/03/2016.
 */
public class CallAPI extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        String resultToDisplay = "";
        InputStream in = null;

        // HTTP Get
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

        // HTTP Post


        // Parse XML
        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            //result = parseXML(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


        return resultToDisplay;
    }

    protected void onPostExecute(String result) {
        //Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

        //intent.putExtra(EXTRA_MESSAGE, result);

        //startActivity(intent);
    }
}
