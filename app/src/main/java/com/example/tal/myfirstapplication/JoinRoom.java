package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by tal on 3/11/2016.
 */
public class JoinRoom extends Activity implements View.OnClickListener {

    TableLayout tableLayout;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        new GetRooms().execute(Constants.GET_ROOMS_API_URL);

    }

    @Override
    public void onClick(View v) {
        if (v.getClass() == ImageButton.class){
            displayRoom(v);
        }else{
            String roomName = ((TextView) ((TableRow) v).getVirtualChildAt(0)).getText().toString();
            new AddMeToRoom().execute(Constants.ADD_PLAYER_TO_ROOM_API_URL,roomName);
        }
    }
    public class AddMeToRoom extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return Requests.getInstance().doPost(params[0],params[1]);
        }
    }
    private class GetRoomDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString = Constants.GET_ROOM_DETAILS_API_URL;
            String roomName = params[0];
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
                bf.write(roomName);
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

    private class GetRooms extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            return Requests.getInstance().doGet(urlString);
        }

        protected void onPostExecute(String result) {

            tableLayout = (TableLayout) findViewById(R.id.AllRooms);
            String[] rooms = result.split(",");
            rooms[0] = rooms[0].substring(1);
            StringBuilder res = new StringBuilder();
            rooms[rooms.length - 1] = rooms[rooms.length - 1].substring(0, rooms[rooms.length - 1].length() - 1);


            for (int i = 0; i < rooms.length; i++) {

                rooms[i] = rooms[i].substring(1, rooms[i].length() - 1);
                TableRow row = new TableRow(JoinRoom.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setOnClickListener(new JoinRoom());
                TextView name = new TextView(JoinRoom.this);
                name.setTextSize(30);
                name.setText(rooms[i]);
                ImageButton but = new ImageButton(JoinRoom.this);
                but.setBackground( getResources().getDrawable(R.drawable.i));
                but.setOnClickListener(JoinRoom.this);
                row.addView(name);
                row.addView(but);
                row.setOnClickListener(JoinRoom.this);
                tableLayout.addView(row);

/*
                try {
                    URL url = new URL(Constants.GET_ROOM_DETAILS_API_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "text/plain");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bf.write(rooms[i]);
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

                String[] str = res.toString().split(",");
                str[0] = str[0].substring(1);
                str[str.length - 1] = str[str.length - 1].substring(0, str[str.length - 1].length() - 1);

                for (int j=0;j<str.length;j++){
                    str[j] = str[j].substring(1, str[j].length() - 1);
                    TableRow row2 = new TableRow(JoinRoom.this);
                    TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row2.setLayoutParams(lp2);
                    row2.setOnClickListener(new JoinRoom());
                    TextView name2 = new TextView(JoinRoom.this);
                    name2.setTextSize(30);
                    name2.setText(str[j]);
                    row2.addView(name2);
                    row2.setOnClickListener(JoinRoom.this);
                    tableLayout.addView(row);
                }*/
            }
        }
    }

    public void displayRoom(View view) {
        String text = ((TextView) ((TableRow) view.getParent()).getVirtualChildAt(0)).getText().toString();

        Intent intent = new Intent(this, Room.class);
        intent.putExtra(Constants.ID_EXTRA, text);

        startActivity(intent);
    }

    public String[] displayRoom2(View view) {

        String urlString = Constants.GET_ROOM_DETAILS_API_URL;
        String roomName = ((TextView) ((TableRow) view).getVirtualChildAt(0)).getText().toString();
        StringBuilder res = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bf.write(roomName);
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

        String[] players = res.toString().split(",");
        players[0] = players[0].substring(1);
        players[players.length - 1] = players[players.length - 1].substring(0, players[players.length - 1].length() - 1);

        for (int i = 0; i < players.length; i++) {
            players[i] = players[i].substring(1, players[i].length() - 1);
        }
        return players;
    }
}
