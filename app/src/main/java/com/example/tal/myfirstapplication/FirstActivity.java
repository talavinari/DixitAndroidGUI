package com.example.tal.myfirstapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirstActivity extends AppCompatActivity {

    public static final String MY_MESSAGE = "my message";
    public static final String REST_API_PREFIX = "http://10.0.0.6:8080/DixitRESTfulAPI/rest/service/";
    public static final String GET_ROOMS_API_URL = REST_API_PREFIX + "rooms";
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
    public void createNewRoom(View view){
        Intent intent = new Intent(this, SecondActivity.class);

        startActivity(intent);
    }

    public void joinRoom(View view){

    }

    public void changeText(View view){
        Button button = (Button) view;
        EditText editText = (EditText) findViewById(R.id.message);

        String text = button.getText().toString();
        Intent intent;
        if (text.equals("Go to second activity with data")){
//            intent = new Intent(this, SecondActivity.class);
//            intent.putExtra(MY_MESSAGE, editText.getText().toString());
            new CallAPI().execute(GET_ROOMS_API_URL);
        }

        else{
            intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
