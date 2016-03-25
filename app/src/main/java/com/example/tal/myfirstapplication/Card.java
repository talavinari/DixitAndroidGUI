package com.example.tal.myfirstapplication;

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

    public Card(int cardNum, int imageNum, RelativeLayout.LayoutParams layoutParams, ImageView cardPic,Object obj, TextView tv, String tvText) {
        this.cardNum = cardNum;
        this.imageNum = imageNum;
        this.layoutParams = layoutParams;
        this.cardPic = cardPic;
        cardPic.setOnClickListener((View.OnClickListener) obj);
        cardPic.setOnLongClickListener((View.OnLongClickListener) obj);
        cardPic.setOnDragListener((View.OnDragListener) obj);
        this.tv = tv;
        this.tv.setText(tvText);
    }


    public Card() {
    }

    public void setVisibility(int i){
        cardPic.setVisibility(i);

        tv.setVisibility(i);
    }

    public void setText(String text){
        this.tv.setText(text);
    }

    private void setTextAlign(){
//        RelativeLayout.LayoutParams lay = (RelativeLayout.LayoutParams) tv.getLayoutParams();
//        lay.addRule(RelativeLayout.CENTER_HORIZONTAL,cardPic.getId());
//        lay.addRule(RelativeLayout.CENTER_VERTICAL,cardPic.getId());
//        tv.setLayoutParams(lay);
    }

    public void bringToFront(){
        cardPic.bringToFront();
        tv.bringToFront();
    }




}
