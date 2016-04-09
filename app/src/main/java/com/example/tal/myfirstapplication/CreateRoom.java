package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

    public class CreateRoom extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_create_room);
    }

    public void createRoom(View view) {
        EditText editText = (EditText) findViewById(R.id.RoomName);

        new AddRoom(this, editText.getText().toString()).execute(UserData.getInstance().getNickName(this));
        UserData.getInstance().setCurrRoom(editText.getText().toString(), this);
    }

    private class AddRoom extends AsyncTask<String, String, String> {
        private Context context;
        private String roomName;
        protected int doInBackgroundExitCode;

        public AddRoom(Context context, String roomName) {
            this.context = context.getApplicationContext();
            this.roomName = roomName;
        }

        @Override
        protected String doInBackground(String... params) {
            String nickName = params[0];
            JSONObject sendingJSON = new JSONObject();

            try {
                sendingJSON.put(Constants.NAME_FIELD, nickName);
                sendingJSON.put(Constants.ROOM_FIELD, roomName);
                String responseJSON = Requests.doPostWithResponse(Constants.ADD_ROOM_API_URL, sendingJSON);

                JSONObject response = new JSONObject(responseJSON);
                if (!response.has(Constants.ERROR)) {
                    Game.initGame();
                    String cards = (String) response.get("cards");
                    UserData.getInstance().setCards(cards);

                    Game.getGame().addPlayer(new Player(nickName, 1));
                    Game.getGame().setFirstStoryTeller();

                    doInBackgroundExitCode = 0;
                    return "";
                }
                else{
                    doInBackgroundExitCode = response.getInt(Constants.ERROR_CODE);
                    return  response.getString(Constants.ERROR);
                }
            } catch (JSONException e) {
                doInBackgroundExitCode = -1;
                return e.getMessage();
            }

        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.isEmpty()) {
                startGameActivity();
            }
            else{
                handleError(s);
            }
        }

        private void startGameActivity() {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.putExtra(Constants.OPERATION_TYPE, Constants.REGISTER_OPERATION);
            intent.putExtra(Constants.TOPIC_ROOM_NAME, roomName);
            startService(intent);

            Intent gameIntent = new Intent(context, GameMain.class);
            startActivity(gameIntent);
        }

        private void handleError(String errorMessage) {
            AlertDialog.Builder builder;
            if (doInBackgroundExitCode == Constants.DUPLICATE_ERROR_CODE) {
                builder = new AlertDialog.Builder(CreateRoom.this).
                        setMessage("The room name you have chosen is already in use. Change your room name")
                        .setCancelable(true).setTitle("Error").setPositiveButton("OK", null);
            }
            else{
                builder = new AlertDialog.Builder(CreateRoom.this).setMessage(errorMessage).
                        setCancelable(true).setTitle(Constants.FATAL_ERROR_TITLE).setPositiveButton("OK", null);
            }

            AlertDialog alert = builder.create();
            alert.show();

        }
    }
}
