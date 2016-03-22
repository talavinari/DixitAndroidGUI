package com.example.tal.myfirstapplication;

import android.content.Context;
import android.location.GpsStatus;
import android.net.sip.SipAudioCall;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by gront on 21/03/2016.
 */
public class Card {
    int imageNum;
    int cardNum;
    RelativeLayout.LayoutParams layoutParams;
    ImageView cardPic;
    private TextView tv;

    public Card(int cardNum, int imageNum, RelativeLayout.LayoutParams layoutParams, ImageView cardPic,Object obj, TextView tv) {
        this.cardNum = cardNum;
        this.imageNum = imageNum;
        this.layoutParams = layoutParams;
        this.cardPic = cardPic;
        cardPic.setOnClickListener((View.OnClickListener) obj);
        cardPic.setOnLongClickListener((View.OnLongClickListener) obj);
        cardPic.setOnDragListener((View.OnDragListener) obj);
        this.tv = tv;
    }

    public void setText(String text){
        this.tv.setText(text);
    }




}
