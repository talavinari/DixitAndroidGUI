package com.example.tal.myfirstapplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

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
    ImageView targetCard;
    private boolean isCardOnTable = false;
    List<Card> cardsInHand = new ArrayList<>();
    public static Card draggedView;
    ClipData data;
    Point po;
    public static Vibrator vib;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        draggedView = new Card();
        cardSize=0;
        po = new Point();
        data = ClipData.newPlainText("", "");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setCardsInPosition();
    }

    @Override
    protected void onStop() {
        this.context = this;
        new OnClose().execute();
        super.onStop();
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
        if (!isCardOnTable) {
            draggedView = cardsInHand.get(getListPlaceByView(v));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            v.setVisibility(View.INVISIBLE);
            GameMain.vib.vibrate(20);
            targetCard.setVisibility(View.VISIBLE);
            targetCard.bringToFront();
            isCardOnTable=true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                draggedView.cardPic.setVisibility(View.VISIBLE);
                setAllCards(cardSize,cardSize*2);
                break;
            case DragEvent.ACTION_DROP:
                if (findViewById(R.id.table) == v){
                    int i = getListPlaceByView(draggedView.cardPic);
                    if (i >= 0){
                        cardsInHand.remove(i);
                        draggedView.cardPic.setLayoutParams(getOutgoingCardLayoutParams());
                        rearrangeCards();
                        draggedView.bringToFront();
                    }
                }
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

        ImageView plr1 = (ImageView) findViewById(R.id.user1);
        ImageView plr2 = (ImageView) findViewById(R.id.user2);
        ImageView plr3 = (ImageView) findViewById(R.id.user3);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(100,100);
        lp1.leftMargin = plr1.getLayoutParams().width/2;
        lp1.topMargin = po.y / 5;
        plr1.setLayoutParams(lp1);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(100,100);
        lp2.rightMargin = plr2.getLayoutParams().width/2;
        lp2.topMargin = po.y / 5;
        plr2.setLayoutParams(lp2);

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(100,100);
        lp3.leftMargin = po.x/2;
        lp3.topMargin = plr3.getLayoutParams().width/2;
        plr3.setLayoutParams(lp3);


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
        cardsInHand.get(i).bringToFront();
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
    private class OnClose extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONObject jobj = new JSONObject();
            try {
                jobj.put(Constants.ROOM_FIELD,UserData.getInstance().getCurrRoom(context));
                jobj.put(Constants.NAME_FIELD,UserData.getInstance().getNickName(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Requests.getInstance().doPost(Constants.REMOVE_PLAYER, jobj);

            return null;
        }
    }
}


