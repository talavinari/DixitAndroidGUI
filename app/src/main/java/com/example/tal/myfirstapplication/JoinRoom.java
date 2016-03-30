package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/11/2016.
 */
public class JoinRoom extends Activity implements View.OnClickListener {

    TableLayout tableLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    BroadcastReceiver reciver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        reciver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                handleError(context);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(reciver,new IntentFilter(QuickstartPreferences.ERROR_IN_JOIN_ROOM));

        new GetRooms().execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void handleError(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                            // On
                            // clicking
                            // "No"
                            // button
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(context, FirstLogIn.class);
                                startService(intent);
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


            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra(Constants.TOPIC_ROOM_NAME, roomName);
                startService(intent);
            }
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

    public class AddMeToRoom extends BaseTask {

        public AddMeToRoom(Context context) {
            super(context.getApplicationContext());
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                GameState.initGame();
                String responseJSON = Requests.doPostWithResponse(Constants.ADD_PLAYER_TO_ROOM_API_URL, getBasicInfoJSON());
                parseJsonResponse(responseJSON);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        private void parseJsonResponse(String json) {
            try {

                JSONObject response = new JSONObject(json);
                if (!response.has("error")) {

                    String cards = (String) response.get("cards");
                    UserData.getInstance().setCards(cards);

                    JSONArray players = ((JSONArray) response.get("players"));
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject playerJSON = players.getJSONObject(i);
                        String playerName = playerJSON.getString("name");

                        Player p = attachImageToPlayer(new Player(playerName,
                                playerJSON.getInt("index"), playerName.equals(UserData.getInstance().getNickName(context)), null));
                        GameState.getGame().addPlayer(p);
                    }

                    GameState.getGame().setFirstStoryTeller();
                } else {
                    Intent intent = new Intent(QuickstartPreferences.ERROR_IN_JOIN_ROOM);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(context, GameMain.class);
            startActivity(intent);
        }
    }

    private Player attachImageToPlayer(Player player){
        if(((TextView)findViewById(R.id.username1)).getText().equals("username1")){
            ((TextView)findViewById(R.id.username1)).setText(player.name);
            player.userPic = (ImageView) findViewById(R.id.user1);
            player.setVisibility(View.VISIBLE);
        }else if(((TextView)findViewById(R.id.username2)).getText().equals("username2")){
            ((TextView)findViewById(R.id.username3)).setText(player.name);
            player.userPic = (ImageView) findViewById(R.id.user3);
            player.setVisibility(View.VISIBLE);
        }else if(((TextView)findViewById(R.id.username3)).getText().equals("username3")){
            ((TextView)findViewById(R.id.username3)).setText(player.name);
            player.userPic = (ImageView) findViewById(R.id.user3);
            player.setVisibility(View.VISIBLE);
        }
        return player;
    }

    private class ChangeName extends BaseTask {
        public ChangeName(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(String... params) {


            return null;
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
                    tableLayout.addView(row);
                }
            } else {
                TableRow tr = new TableRow(JoinRoom.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                tr.setLayoutParams(lp);
                TextView tv = new TextView(JoinRoom.this);
                tv.setTextSize(30);
                tv.setText("NO ROOMS TO SHOW!");
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


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(Constants.TAG_MAIN_CLASS, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


}
