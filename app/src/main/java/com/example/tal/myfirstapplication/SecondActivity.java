package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by tal on 3/11/2016.
 */
public class SecondActivity extends Activity {

    public static final String MY_MESSAGE = "my message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(MY_MESSAGE);
        TextView textView = new TextView(this);
        textView.setText(stringExtra);
        textView.setTextSize(45);
        setContentView(textView);
        //setContentView(R.layout.second_activity_layout);
    }
}
