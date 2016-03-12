package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
 * Created by tal on 3/11/2016.
 */
public class ThirdActivity extends Activity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity_layout);

        new GetRooms().execute(Constants.GET_ROOMS_API_URL);

    }
/*
    private void initGrid() {
        tableLayout = (TableLayout) findViewById(R.id.tableLayout1);
        for (int i = 0; i <2; i++) {

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView id = new TextView(this);
            TextView name = new TextView(this);
            name.setText("Tal " + i);
            id.setText(String.valueOf(i));
            row.addView(id);
            row.addView(name);
            tableLayout.addView(row, i);
        }
    }*/
    private class GetRooms extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String resultToDisplay = "";
            InputStream in = null;

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
//            XmlPullParserFactory pullParserFactory;
//
//            try {
//                pullParserFactory = XmlPullParserFactory.newInstance();
//                XmlPullParser parser = pullParserFactory.newPullParser();
//                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                parser.setInput(in, null);
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
//            return resultToDisplay;
        }


        protected void onPostExecute(String result) {

            tableLayout = (TableLayout) findViewById(R.id.AllRooms);
            String[] rooms = result.split(",");
            rooms[0] = rooms[0].substring(1);
            rooms[rooms.length-1] = rooms[rooms.length-1].substring(0,rooms[rooms.length-1].length()-1);


            for (int i = 0; i <rooms.length; i++) {

                rooms[i] = rooms[i].substring(1,rooms[i].length()-1);
                TableRow row= new TableRow(ThirdActivity.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView name = new TextView(ThirdActivity.this);
                name.setText(rooms[i]);
                row.addView(name);
                tableLayout.addView(row, i);
            }
        }
    }
}
