package com.example.tal.myfirstapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Room extends AppCompatActivity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String roomName = getIntent().getExtras().getString(Constants.ID_EXTRA);

        ((TextView)findViewById(R.id.roomname)).setText(roomName);

        new GetRoomDetails().execute(Constants.GET_ROOM_DETAILS_API_URL, roomName);
    }

    public String getRoom(String roomName){
        return new getPlayersForRoom().execute(roomName).toString();
    }


    private class GetRoomDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String roomName = params[1];

            return Requests.getInstance().doPostWithResponse(urlString, roomName);
        }

        protected void onPostExecute(String result) {
            tableLayout = (TableLayout) findViewById(R.id.allPlayers);
            String[] rooms = result.split(",");
            rooms[0] = rooms[0].substring(1);
            rooms[rooms.length-1] = rooms[rooms.length-1].substring(0,rooms[rooms.length-1].length()-1);

            for (int i = 0; i <rooms.length; i++) {

                rooms[i] = rooms[i].substring(1,rooms[i].length()-1);
                TableRow row= new TableRow(Room.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setOnClickListener(new JoinRoom());
                TextView name = new TextView(Room.this);
                name.setText(rooms[i]);
                row.addView(name);
                tableLayout.addView(row, i);
            }
        }
    }

    private class getPlayersForRoom extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String urlString = Constants.GET_ROOM_DETAILS_API_URL;

            StringBuilder res = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "text/plain");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bf.write(params[0]);
                bf.flush();
                bf.close();
                outputStream.close();
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return res.toString();
        }
    }
}
