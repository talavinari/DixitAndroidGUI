package com.example.tal.myfirstapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Room extends AppCompatActivity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        String roomName = getIntent().getExtras().getString(Constants.ID_EXTRA);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((TextView)findViewById(R.id.roomname)).setText(roomName);

        new GetRoomDetails().execute(Constants.GET_ROOM_DETAILS_API_URL, roomName);
    }

    private class GetRoomDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String roomName = params[1];
            urlString = urlString + "/" + roomName;

            return Requests.doGet(urlString);
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
}
