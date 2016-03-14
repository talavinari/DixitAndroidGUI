package com.example.tal.myfirstapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Room extends AppCompatActivity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        new GetRoomDetails().execute(Constants.GET_ROOM_DETAILS_API_URL,"","1");
    }


    private class GetRoomDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String roomName = params[1];
            String roomId = params[2];
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
        }


        protected void onPostExecute(String result) {

            tableLayout = (TableLayout) findViewById(R.id.AllRooms);
            String[] rooms = result.split(",");
            rooms[0] = rooms[0].substring(1);
            rooms[rooms.length-1] = rooms[rooms.length-1].substring(0,rooms[rooms.length-1].length()-1);


            for (int i = 0; i <rooms.length; i++) {

                rooms[i] = rooms[i].substring(1,rooms[i].length()-1);
                TableRow row= new TableRow(Room.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setOnClickListener(new ThirdActivity());
                TextView name = new TextView(Room.this);
                name.setText(rooms[i]);
                row.addView(name);
                tableLayout.addView(row, i);
            }
        }
    }
}
