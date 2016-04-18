package com.example.tal.myfirstapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class Player {
    String name;
    TextView username;
    ImageView userPic;
    int index;
    int score = 0;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public void setVisibility(int i){
        username.setVisibility(i);
        userPic.setVisibility(i);
    }

    public void hideText(){
        username.setVisibility(View.INVISIBLE);
    }
    public void showText(){
        username.setVisibility(View.VISIBLE);
    }

}
