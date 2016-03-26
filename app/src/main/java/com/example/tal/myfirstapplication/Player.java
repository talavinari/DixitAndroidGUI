package com.example.tal.myfirstapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gront on 24/03/2016.
 */
public class Player {
    List<Card> cardsInHeand;
    String Association;
    String name;
    TextView username;
    ImageView userPic;
    int index;
    int score = 0;
    int pickedCard;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Player(TextView username, ImageView userPic, String name, int index) {
        this.username = username;
        this.userPic = userPic;
        this.name = name;
        this.index = index;

        //setVisibility(View.INVISIBLE);
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


    //List<Card> cardsInHeand;


}
