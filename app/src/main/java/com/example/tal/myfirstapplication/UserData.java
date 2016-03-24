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

    public void setCards(String string) {
        string.replace('"',' ');

        String[] cards = string.split(",");

        cards[0] = cards[0].substring(1);
        cards[cards.length - 1] = cards[cards.length - 1].substring(0, cards[cards.length - 1].length() - 1);
        cards[0].trim();
        cards[1].trim();
        cards[2].trim();
        cards[3].trim();
        cards[4].trim();
        cards[5].trim();



        this.cards = cards;
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
