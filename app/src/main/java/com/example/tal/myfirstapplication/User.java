package com.example.tal.myfirstapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by gront on 24/03/2016.
 */
public class User {
    String name;
    List<Card> cardsInHeand;
    String Association;
    int pickedCard;
    ImageView userPic;
    TextView username;

    public User(TextView username, ImageView userPic, String name) {
        this.username = username;
        this.userPic = userPic;
        this.name = name;

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
}
