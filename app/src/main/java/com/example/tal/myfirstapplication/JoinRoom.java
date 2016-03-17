package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        displayRoom(v);
    }
    public class AddMeToRoom extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
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
            rooms[rooms.length - 1] = rooms[rooms.length - 1].substring(0, rooms[rooms.length - 1].length() - 1);


            for (int i = 0; i < rooms.length; i++) {

                rooms[counter] = rooms[counter].substring(1, rooms[counter].length() - 1);
                TableRow row = new TableRow(JoinRoom.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setOnClickListener(new JoinRoom());
                TextView name = new TextView(JoinRoom.this);
                name.setTextSize(30);
                name.setText(rooms[i]);
                row.addView(name);
                row.setOnClickListener(JoinRoom.this);
                tableLayout.addView(row, counter);
                counter++;
                //displayRoom2(row);
            }
        }
    }

    public void displayRoom(View view) {
        String text = ((TextView) ((TableRow) view).getVirtualChildAt(0)).getText().toString();

        Intent intent = new Intent(this, Room.class);
        intent.putExtra(Constants.ID_EXTRA, text);

        startActivity(intent);
    }

    public void displayRoom2(View view) {
        Room room = new Room();
        String res = room.getRoom(((TextView) ((TableRow) view).getVirtualChildAt(0)).getText().toString());

        String[] players = res.split(",");
        players[0] = players[0].substring(1);
        players[players.length - 1] = players[players.length - 1].substring(0, players[players.length - 1].length() - 1);

        for (int i = 0; i < players.length; i++) {

            players[i] = players[i].substring(1, players[i].length() - 1);
            TableRow row = new TableRow(JoinRoom.this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setOnClickListener(new JoinRoom());
            TextView name = new TextView(JoinRoom.this);
            name.setTextSize(30);
            name.setText("      " + players[i]);
            row.addView(name);
            row.setBackgroundColor(Color.LTGRAY);
            tableLayout.addView(row, counter);
            counter++;
        }
    }
}
