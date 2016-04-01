package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gront on 19/03/2016.
 */
public class UserData extends Activity {

    private static UserData userData;
    private List<String> cards;
    private String nickName;
    private Bitmap bmp;

    private UserData(){
        cards = new ArrayList<>();
    }

    public static UserData getInstance(){
        if (userData == null){
            userData = new UserData();
        }

        return userData;
    }

    public List<String> getCards() {
        return cards;
    }

    public void setCards(String cardsInput) {
        if (cardsInput.length() > 0) {
            String[] cards = cardsInput.split(Constants.DELIMITER);

            for (int i=0;i<Constants.NUMBER_OF_CARDS_IN_HAND; i++){
                this.cards.add(cards[i].trim());
            }
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

    public Bitmap getBmp(Context context) {

        SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
        String tmp =sp1.getString("Pic", null);
        if (tmp != null){
            StringToBitMap(tmp);
        }else{
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_pic);
        }

        return bmp;
    }

    public void setBmp(Bitmap bmp, Context context) {
        this.bmp = bmp;

        SharedPreferences sp= context.getSharedPreferences("Login", 0);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("Pic", BitMapToString(bmp));
        Ed.commit();
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

    public void removeCard(String card) {
        cards.remove(card);
    }

    public void addCard(String card) {
        cards.add(card);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
