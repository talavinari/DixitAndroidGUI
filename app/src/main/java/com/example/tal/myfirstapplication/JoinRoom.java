package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/11/2016.
 */
public class JoinRoom extends Activity implements View.OnClickListener {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        new GetRooms().execute(Constants.GET_ROOMS_API_URL);

    }

    @Override
    public void onClick(View v) {
        if (v.getClass() == ImageButton.class) {
            displayRoom(v);
        } else {
            String roomName = ((TextView) ((TableRow) v).getVirtualChildAt(0)).getText().toString();
            UserData.getInstance().setCurrRoom(roomName, this);

            new AddMeToRoom(this).execute(Constants.ADD_PLAYER_TO_ROOM_API_URL, UserData.getInstance().getNickName(this), roomName);

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra(Constants.TOPIC_ROOM_NAME, roomName);
                startService(intent);
            }

        }
    }

    public class AddMeToRoom extends AsyncTask<String, String, String> {
        Context context;

        public AddMeToRoom(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jobj = new JSONObject();
            try {
                jobj.put("nickName", params[1]);
                jobj.put("roomName", params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = Requests.getInstance().doPostWithResponse(params[0], jobj);
            parseJsonResponse(json);

            return "";
        }

        private void parseJsonResponse(String json) {
            try {
                JSONObject response = new JSONObject(json);
                String cards = (String)response.get("cards");
                UserData.getInstance().setCards(cards);

                JSONArray players = ((JSONArray) response.get("players"));
                for (int i = 0; i < players.length(); i++) {
                    JSONObject playerJSON = players.getJSONObject(i);
                    Player p = new Player(playerJSON.getString("name"),
                                          playerJSON.getInt("index"), false);
                    GameState.getGame().addPlayer(p);
                }

                GameState.getGame().setFirstStoryTeller();

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

    @Override
    protected void onResume() {
        super.onResume();
        new GetRooms().execute(Constants.GET_ROOMS_API_URL);
    }

    private class GetRooms extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            return Requests.getInstance().doGet(urlString);
        }

        protected void onPostExecute(String result) {

            tableLayout = (TableLayout) findViewById(R.id.AllRooms);
            tableLayout.removeAllViews();
            if (result != null) {


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
            }else{
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
