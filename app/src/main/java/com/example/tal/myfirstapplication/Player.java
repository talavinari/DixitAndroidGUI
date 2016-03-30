package com.example.tal.myfirstapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by gront on 24/03/2016.
 */
public class Player {
    String name;
    TextView username;
    ImageView userPic;
    int index;
    int score = 0;
    boolean currentAndroidUserIndication;

    public Player(String name, int index, boolean isCurrentUser,ImageView userPic) {
        this.name = name;
        this.index = index;
        this.currentAndroidUserIndication = isCurrentUser;
        this.userPic = userPic;
    }

    public void setVisibility(int i){
        username.setVisibility(i);
        username.setVisibility(i);
    }

    public void hideText(){
        username.setVisibility(View.INVISIBLE);
    }
    public void showText(){
        username.setVisibility(View.VISIBLE);
    }

}
