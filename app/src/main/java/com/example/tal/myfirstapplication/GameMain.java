package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameMain extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {
    int sizeW;
    int sizeH;
    int cardSize;
    private float mDownX;
    ImageView targetCard;
    private float mDownY;
    private boolean isOnClick;
    List<Card> cardsInHand = new ArrayList<>();
    public static View draggedView;
    View cardStartPlace;
    ClipData data;
    Point po;
    public static Vibrator vib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        draggedView = new View(this);
        cardSize=0;
        po = new Point();
        data = ClipData.newPlainText("", "");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setCardsInPosition();
    }

    @Override
    public void onClick(View v) {
        if (v.getClass() == ImageView.class) {
            int i =  getListPlaceByView(v);
            if (v.getLayoutParams().height < cardSize * 5 && i >=0) {
                setBigCardLayout(getListPlaceByView(v));
            } else {
                setAllCards(cardSize, cardSize*2);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        draggedView = v;
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        v.startDrag(data, shadowBuilder, v, 0);
        v.setVisibility(View.INVISIBLE);
        GameMain.vib.vibrate(20);
        targetCard.setVisibility(View.VISIBLE);
        targetCard.bringToFront();
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                draggedView.setVisibility(View.VISIBLE);
                setAllCards(cardSize,cardSize*2);
                break;
            case DragEvent.ACTION_DROP:
                if (findViewById(R.id.table) == v){
                    int i = getListPlaceByView(draggedView);
                    if (i >= 0){
                        cardsInHand.remove(i);
                        draggedView.setLayoutParams(getOutgoingCardLayoutParams());
                        rearrangeCards();
                        draggedView.bringToFront();
                    }
                }
                //targetCard.setVisibility(View.INVISIBLE);
                draggedView.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                targetCard.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    public void setCardsInPosition() {

        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card1),this, (TextView) findViewById(R.id.card1text)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card2),this, (TextView) findViewById(R.id.card2text)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card3),this, (TextView) findViewById(R.id.card3text)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card4),this, (TextView) findViewById(R.id.card4text)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card5),this, (TextView) findViewById(R.id.card5text)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card6),this, (TextView) findViewById(R.id.card6text)));
        calcSize();

        targetCard = (ImageView) findViewById(R.id.target);
        targetCard.setLayoutParams(getOutgoingCardLayoutParams());
        targetCard.setVisibility(View.INVISIBLE);
        targetCard.bringToFront();


        setAllCards(cardSize, cardSize*2);
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.get(i).cardPic.setOnClickListener(this);
            cardsInHand.get(i).cardPic.setOnLongClickListener(this);
            cardsInHand.get(i).cardPic.setOnDragListener(this);
        }

        ImageView table = (ImageView) findViewById(R.id.table);
        RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(table.getLayoutParams().width, sizeH * 5 / 7);
        r.addRule(RelativeLayout.ALIGN_PARENT_START);
        table.setLayoutParams(r);
        table.setOnDragListener(this);
    }

    private void setAllCards(int w, int h) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.get(i).cardPic.setLayoutParams(getLayoutParams(w, h, i));
        }
    }

    private int getListPlaceByView(View v) {
        for (int i = 0; i < cardsInHand.size(); i++){
            if (cardsInHand.get(i).cardPic == v){
                return i;
            }
        }
        return -1;
    }

    private RelativeLayout.LayoutParams getLayoutParams(int w, int h, int i) {
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(w, h);
        labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        labelLayoutParams.bottomMargin = cardSize;
        labelLayoutParams.leftMargin = (i * (cardSize + sizeW)) + sizeW;
        return labelLayoutParams;
    }

    private void setBigCardLayout(int i){

        //setAllCards(cardSize / 2, cardSize);

        int tmpCardSize = cardSize/2;
        int bigCardSize = cardSize * 5;
        int tmpWideSize = (po.x - (bigCardSize + (cardsInHand.size()*tmpCardSize)))/(cardsInHand.size() +1);
        RelativeLayout.LayoutParams labelLayoutParams = null;

        for (int j =0; j< cardsInHand.size();j++){
            if (j<i){
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, tmpCardSize*2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;
            }
            else if (j==i){
                labelLayoutParams = new RelativeLayout.LayoutParams(bigCardSize, bigCardSize*2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;

            }else{
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, tmpCardSize*2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = ((j-1) * (tmpCardSize + tmpWideSize)) + (2*tmpWideSize)+bigCardSize;

            }
            cardsInHand.get(j).cardPic.setLayoutParams(labelLayoutParams);
        }
    }

    private RelativeLayout.LayoutParams getOutgoingCardLayoutParams() {
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(cardSize, cardSize*2);
        labelLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        labelLayoutParams.topMargin = 8*cardSize;
        return labelLayoutParams;
    }

    private void rearrangeCards() {
        calcSize();
        setAllCards(cardSize, cardSize * 2);
    }

    private void calcSize() {
        this.getWindowManager().getDefaultDisplay().getSize(po);

        if (cardSize == 0) {
            sizeW = po.x / (3*cardsInHand.size() + 1);
            cardSize = 2 * sizeW;
        }else{
            sizeW = ((po.x -(cardsInHand.size()*cardSize)) / (cardsInHand.size() + 1));
        }
        sizeH = po.y;
    }
}


