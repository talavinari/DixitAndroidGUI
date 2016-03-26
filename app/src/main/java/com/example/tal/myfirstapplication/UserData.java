package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by gront on 19/03/2016.
 */
public class UserData extends Activity {

    private static UserData userData;

    private UserData(){}
    private String[] cards;

    public static UserData getInstance(){
        if (userData == null){
            userData = new UserData();
        }
        return userData;
    }

    private String nickName;

    public String[] getCards() {
        return cards;
    }

    public void setCards(String cardsInput) {
        if (cardsInput.length() > 0) {
            String[] cards = cardsInput.split(Constants.DELIMITER);

            for (int i=0;i<Constants.NUMBER_OF_CARDS_IN_HAND; i++){
                cards[i] = cards[i].trim();
            }

            this.cards = cards;
        }
    }

    private String currRoom;

    public String getNickName(Context context) {
        if (nickName==null) {

            SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
            nickName = sp1.getString("nickName", null);
        }
        return nickName;
    }

    public String getCurrRoom(Context context) {

        if (currRoom==null) {

            SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
            currRoom = sp1.getString("currRoom", null);
        }

        return currRoom;
    }

    public void setNickName(String nickName, Context context) {

        SharedPreferences sp= context.getSharedPreferences("Login", 0);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("nickName", nickName);
        Ed.commit();

        this.nickName = nickName;
    }

    public void setCurrRoom(String currRoom,Context context) {

        SharedPreferences sp= context.getSharedPreferences("Login", 0);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("currRoom", currRoom);
        Ed.commit();

        this.currRoom = currRoom;
    }



}
