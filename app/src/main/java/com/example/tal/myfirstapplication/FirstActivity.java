package com.example.tal.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Getting the ip of the machine
        Constants con = new Constants();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_layout);



    }
    public void createNewRoom(View view){
        Intent intent = new Intent(this, SecondActivity.class);

        startActivity(intent);
    }

    public void joinRoom(View view){
        Intent intent = new Intent(this, ThirdActivity.class);

        startActivity(intent);
    }

    public void changeText(View view){
        Button button = (Button) view;

        String text = button.getText().toString();
        Intent intent;
        if (text.equals("Go to second activity with data")){
//            intent = new Intent(this, SecondActivity.class);
//            intent.putExtra(MY_MESSAGE, editText.getText().toString());
            //.execute(Constants.GET_ROOMS_API_URL);
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
