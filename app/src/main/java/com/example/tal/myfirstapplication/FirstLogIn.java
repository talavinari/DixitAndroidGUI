package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class FirstLogIn extends Activity {

    Bitmap bmp;
    EditText inputUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_log_in);
        inputUserName = (EditText) findViewById(R.id.editText);
        String nickName = UserData.getInstance().getNickName(this);
        if (nickName != null){
            inputUserName.setText(nickName);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void signIn(View view){
        UserData.getInstance().setNickName(inputUserName.getText().toString(), this);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void takePic(View v){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, 0);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Camera cam = new Camera();

        if ( data != null && data.getExtras() !=  null) {
            bmp = (Bitmap) data.getExtras().get("data");

            ((ImageView)findViewById(R.id.mypic)).setImageBitmap(bmp);
            UserData.getInstance().setBmp(bmp,this);
        }
    }

}
