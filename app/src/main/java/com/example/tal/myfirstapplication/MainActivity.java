package com.example.tal.myfirstapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Getting the ip of the machine
        Constants con = new Constants();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Registering BroadcastReceiver


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        };

        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_first, menu);



        return true;
    }
    public void createNewRoom(View view){
        Intent intent = new Intent(this, CreateRoom.class);

        startActivity(intent);
    }

    public void joinRoom(View view){
        Intent intent = new Intent(this, JoinRoom.class);

        startActivity(intent);
    }

    public void changeText(View view){
        Button button = (Button) view;

        String text = button.getText().toString();
        Intent intent;
        if (text.equals("Go to second activity with data")){
//            intent = new Intent(this, CreateRoom.class);
//            intent.putExtra(MY_MESSAGE, editText.getText().toString());
            //.execute(Constants.GET_ROOMS_API_URL);
        }

        else{
            intent = new Intent(this, JoinRoom.class);
            startActivity(intent);
        }
    }

    public void settings(){
        Intent intent = new Intent(this,FirstLogIn.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
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
