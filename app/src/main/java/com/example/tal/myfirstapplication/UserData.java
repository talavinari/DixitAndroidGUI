package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

/**
 * Created by gront on 19/03/2016.
 */
public class UserData extends Activity {

    private static UserData userData;

    private UserData(){}

    public static UserData getInstance(){
        if (userData == null){
            userData = new UserData();
        }
        return userData;
    }

    private String nickName;
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
