package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JoinRoom extends Activity implements View.OnClickListener, View.OnTouchListener {

    int backColor;
    TableLayout tableLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        new GetRooms().execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void handleError(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The name you have chosen is already in use. What next?")
                .setCancelable(false)
                .setPositiveButton(("Pick another room"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        })
                .setNegativeButton(("Pick another name"),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(context, FirstLogIn.class);
                                startActivity(intent);
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {

        if (v.getClass() == ImageButton.class) {
            displayRoom(v);
        } else {
            String roomName = ((TextView) ((TableRow) v).getVirtualChildAt(0)).getText().toString();

            UserData.getInstance().setCurrRoom(roomName, this);

            new AddMeToRoom(this).execute();
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra(Constants.TOPIC_ROOM_NAME, roomName);
            startService(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "JoinRoom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.tal.myfirstapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "JoinRoom Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.tal.myfirstapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            backColor = v.getSolidColor();
            v.setBackgroundColor(Color.LTGRAY);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(backColor);
        }

        return false;
    }

    public class AddMeToRoom extends BaseTask {

        public AddMeToRoom(Context context) {
            super(context.getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            Game.initGame();
            String responseJSON;
            try {
                responseJSON = Requests.doPostWithResponse(Constants.ADD_PLAYER_TO_ROOM_API_URL, getBasicInfoJSON());
                return Boolean.toString(parseJsonResponse(responseJSON));

            } catch (JSONException e) {
                e.printStackTrace();
                return Boolean.toString(false);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Boolean.parseBoolean(s)) {
                Intent intent = new Intent(context, GameMain.class);
                startActivity(intent);
            } else {
                handleError(JoinRoom.this);
            }
        }


        private boolean parseJsonResponse(String json) {
            try {

                JSONObject response = new JSONObject(json);
                if (!response.has("error")) {

                    String cards = (String) response.get("cards");
                    UserData.getInstance().setCards(cards);

                    JSONArray players = ((JSONArray) response.get("players"));
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject playerJSON = players.getJSONObject(i);
                        String playerName = playerJSON.getString("name");

                        Player p = new Player(playerName, playerJSON.getInt(Constants.INDEX));
                        Game.getGame().addPlayer(p);
                    }

                    Game.getGame().setFirstStoryTeller();
                    return true;
                } else {
                    return false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetRooms().execute();
    }

    private class GetRooms extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return Requests.doGet(Constants.GET_ROOMS_API_URL);
        }

        protected void onPostExecute(String result) {

            tableLayout = (TableLayout) findViewById(R.id.AllRooms);
            tableLayout.removeAllViews();
            if (!result.equals("[]")) {
                String[] rooms = result.split(",");
                rooms[0] = rooms[0].substring(1);
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
                    but.setBackground(getResources().getDrawable(R.drawable.i));
                    but.setOnClickListener(JoinRoom.this);
                    row.addView(name);
                    row.addView(but);
                    row.setOnClickListener(JoinRoom.this);
                    row.setOnTouchListener(JoinRoom.this);
                    tableLayout.addView(row);
                }
            } else {
                TableRow tr = new TableRow(JoinRoom.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                tr.setLayoutParams(lp);
                TextView tv = new TextView(JoinRoom.this);
                tv.setTextSize(30);
                tv.setText(R.string.noRoomToShow);
                tr.addView(tv);
                tableLayout.addView(tr);
            }
        }
    }

    public void displayRoom(View view) {
        String text = ((TextView) ((TableRow) view.getParent()).getVirtualChildAt(0)).getText().toString();

        Intent intent = new Intent(this, Room.class);
        intent.putExtra(Constants.ID_EXTRA, text);

        startActivity(intent);
    }
}
