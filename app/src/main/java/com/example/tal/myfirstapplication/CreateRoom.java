package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tal on 3/11/2016.
 */
public class CreateRoom extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
    }

    public void createRoom(View view) {
        EditText editText = (EditText) findViewById(R.id.RoomName);

        new AddRoom(this).execute(editText.getText().toString(), UserData.getInstance().getNickName(this));
        UserData.getInstance().setCurrRoom(editText.getText().toString(), this);
    }

    private class AddRoom extends AsyncTask<String, String, String> {
        Context context;

        public AddRoom(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... params) {
            String roomName = params[0];
            String nickName = params[1];
            JSONObject sendingJSON = new JSONObject();

            try {
                sendingJSON.put(Constants.NAME_FIELD, nickName);
                sendingJSON.put(Constants.ROOM_FIELD, roomName);

                GameState.initGame();
                String responseJSON = Requests.doPostWithResponse(Constants.ADD_ROOM_API_URL, sendingJSON);
                JSONObject response = new JSONObject(responseJSON);
                String cards = (String) response.get("cards");
                UserData.getInstance().setCards(cards);

                GameState.getGame().addPlayer(new Player(nickName, 1));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result) {
            Intent intent = new Intent(context, GameMain.class);
            startActivity(intent);
        }
    }
}
