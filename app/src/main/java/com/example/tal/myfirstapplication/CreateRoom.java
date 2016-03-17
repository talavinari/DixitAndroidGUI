package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tal on 3/11/2016.
 */
public class CreateRoom extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
    }

    public void createRoom(View view){
        EditText editText = (EditText) findViewById(R.id.RoomName);

        new AddRoom().execute(Constants.ADD_ROOM_API_URL,editText.getText().toString());
    }

    private class AddRoom extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String roomName = params[1];
            String jsonString = roomName +"\n"+"guy12";
            Requests.getInstance().doPost(urlString,roomName);


            try {
                JSONObject jobj = new JSONObject(jsonString);
                urlString = Constants.ADD_PLAYER_TO_ROOM_API_URL;

                return Requests.getInstance().doPost(urlString,jobj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
        }
    }
}
