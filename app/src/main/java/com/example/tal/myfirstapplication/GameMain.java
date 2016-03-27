package com.example.tal.myfirstapplication;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.view.DragEvent;
import android.view.KeyEvent;
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
public class GameMain extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener, View.OnKeyListener{
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
    boolean usr1 = false;
    boolean usr2 = false;
    boolean usr3 = false;
    AnimatorSet anset1;
    AnimatorSet anset2;
    AnimatorSet anset3;
    AnimatorSet antext1;
    AnimatorSet antext2;
    AnimatorSet antext3;
    int draggedCardNum;
    List<Player> tmpPlayers;

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle data = intent.getBundleExtra("message");
                String messageType = data.getString(Constants.MESSAGE_TYPE);
                if (MessageType.Association.getDescription().equals(messageType)){
                    notifyAssociation(data);
                }else if(MessageType.Vote.getDescription().equals(messageType)){
                    notifyVote(data);
                }else if (MessageType.JoinedToRoom.getDescription().equals(messageType)){
                    notifyJoinedToRoom(data);
                }
                else if (MessageType.PickedCard.getDescription().equals(messageType)){
                    notifyPlayerPickedCard(data);
                }
            }
        };
        registerReceiver();
        draggedView = new Card();
        cardSize = 0;
        po = new Point();
        data = ClipData.newPlainText("", "");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setCardsInPosition();
        tmpPlayers = new ArrayList<>();
    }

    private void notifyPlayerPickedCard(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        int pickedCard = data.getInt(Constants.VOTED_CARD);
        GameState.getGame().setPickedCardForPlayer(playerName,pickedCard);
        if (GameState.getGame().allPlayersPicked()){
            handlePickedCardsGUI();
        }

    }


    private void notifyVote(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        int votedCard = data.getInt(Constants.VOTED_CARD);
        GameState game = GameState.getGame();
        game.setVoteForPlayer(playerName, votedCard);
        if (game.votes.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT - 1){
            game.calculateScore();
            game.continueToNextStory();
        }

        updateGUI();
    }


    private void notifyAssociation(Bundle data) {
        //Maybe player name is redundant?
        String playerName = data.getString(Constants.PLAYER_NAME);
        GameState.getGame().currentWinningCard = Integer.valueOf(data.getString(Constants.WINNING_CARD));
        GameState.getGame().currentAssociation = data.getString(Constants.ASSOCIATION);
        handleAssociationGUI();
    }

    private void handleAssociationGUI() {
        // TODO handle association received GUI
    }


    private void handlePickedCardsGUI() {

    }

    private void updateGUI() {
        //TODO gui of next trun
        setTellerPic();
    }

    private void notifyJoinedToRoom(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        int index = Integer.valueOf(data.getString(Constants.INDEX));
        GameState.getGame().addPlayer(new Player(playerName, index, false));

        if (GameState.getGame().players.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT){
            startGameGUI();
        }
    }

    private void startGameGUI() {
        // TODO handle GUI of start



    }

    @Override
    protected void onStop() {
        this.context = this;
        new OnClose().execute();
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        if (getListPlaceByView(v) != -1) {
            int i = getListPlaceByView(v);
            if (v.getLayoutParams().height < cardSize * 5 && i >= 0) {
                setBigCardLayout(getListPlaceByView(v));
            } else {
                setAllCards(cardSize, cardSize * 2);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isCardOnTable) {
            draggedCardNum = getListPlaceByView(v);
            draggedView = cardsInHand.get(draggedCardNum);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            GameMain.vib.vibrate(20);
            targetCard.setVisibility(View.VISIBLE);
            targetCard.bringToFront();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                draggedView.setVisibility(View.INVISIBLE);
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return false;
            case DragEvent.ACTION_DRAG_EXITED:
                draggedView.setVisibility(View.VISIBLE);
//                cardsInHand.get(draggedCardNum).cardPic.setVisibility(View.VISIBLE);
                isCardOnTable = false;
                setAllCards(cardSize, cardSize * 2);
                return true;
            case DragEvent.ACTION_DROP:
                if (findViewById(R.id.table) == v) {
                        cardsInHand.remove(draggedCardNum);
                        draggedView.cardPic.setLayoutParams(getOutgoingCardLayoutParams());
                        rearrangeCards();
                        draggedView.setVisibility(View.VISIBLE);
                        draggedView.bringToFront();
                        isCardOnTable = true;
//                    draggedView.setVisibility(View.VISIBLE);
                        if (amITheTeller()){
                            findViewById(R.id.association).setVisibility(View.VISIBLE);
                        }
                    return true;
                }
                draggedView.setVisibility(View.VISIBLE);

//                draggedView.setVisibility(View.VISIBLE);
                return false;
            case DragEvent.ACTION_DRAG_ENDED:

                draggedView.cardPic.post(new Runnable() {
                    @Override
                    public void run() {
                        draggedView.setVisibility(View.VISIBLE);
                    }
                });
//                draggedView.setVisibility(View.VISIBLE);
//                cardsInHand.get(draggedCardNum).cardPic.setVisibility(View.VISIBLE);
                return false;
            default:
                return true;
        }
    }

    public void setCardsInPosition() {
        GameState.getGame().addPlayer(new Player((TextView) findViewById(R.id.username1), (ImageView) findViewById(R.id.user1), "",1));
        GameState.getGame().addPlayer(new Player((TextView) findViewById(R.id.username2), (ImageView) findViewById(R.id.user2), "",2));
        GameState.getGame().addPlayer(new Player((TextView) findViewById(R.id.username3), (ImageView) findViewById(R.id.user3), "",3));
        GameState.getGame().addPlayer(new Player(null, null, UserData.getInstance().getNickName(this),0));

        anset1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1movement);
        anset2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2movement);
        anset3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user3movement);

        antext1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1movement);
        antext2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2movement);
        antext3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user3movement);

        anset1.setTarget(GameState.getGame().players.get(1));
        anset2.setTarget(GameState.getGame().players.get(2));
        anset3.setTarget(GameState.getGame().players.get(3));

        setTellerPic();

        handleCards();
        calcSize();

        findViewById(R.id.association).setOnKeyListener(this);

        targetCard = (ImageView) findViewById(R.id.target);
        targetCard.setLayoutParams(getOutgoingCardLayoutParams());
        targetCard.setVisibility(View.INVISIBLE);
        targetCard.bringToFront();

        setAllCards(cardSize, cardSize * 2);
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

    private void handleCards() {
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1),
                (ImageView) findViewById(R.id.card1), this,
                (TextView) findViewById(R.id.card1text),
                UserData.getInstance().getCards()[0]));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1),
                (ImageView) findViewById(R.id.card2), this,
                (TextView) findViewById(R.id.card2text),
                UserData.getInstance().getCards()[1]));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card3), this, (TextView) findViewById(R.id.card3text), UserData.getInstance().getCards()[2]));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card4), this, (TextView) findViewById(R.id.card4text), UserData.getInstance().getCards()[3]));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card5), this, (TextView) findViewById(R.id.card5text), UserData.getInstance().getCards()[4]));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card6), this, (TextView) findViewById(R.id.card6text), UserData.getInstance().getCards()[5]));
    }

    private void setAllCards(int w, int h) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.get(i).cardPic.setLayoutParams(getLayoutParams(w, h, i));
        }
    }

    private int getListPlaceByView(View v) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            if (cardsInHand.get(i).cardPic.getId() == v.getId()) {
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

    private void setBigCardLayout(int i) {
        int tmpCardSize = cardSize / 2;
        int bigCardSize = cardSize * 5;
        int tmpWideSize = (po.x - (bigCardSize + (cardsInHand.size() * tmpCardSize))) / (cardsInHand.size() + 1);
        RelativeLayout.LayoutParams labelLayoutParams = null;

        for (int j = 0; j < cardsInHand.size(); j++) {
            if (j < i) {
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, tmpCardSize * 2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;
            } else if (j == i) {
                labelLayoutParams = new RelativeLayout.LayoutParams(bigCardSize, bigCardSize * 2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;
            } else {
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, tmpCardSize * 2);
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = ((j - 1) * (tmpCardSize + tmpWideSize)) + (2 * tmpWideSize) + bigCardSize;
            }
            cardsInHand.get(j).cardPic.setLayoutParams(labelLayoutParams);
        }
        cardsInHand.get(i).bringToFront();
    }

    private RelativeLayout.LayoutParams getOutgoingCardLayoutParams() {
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(cardSize, cardSize * 2);
        labelLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        labelLayoutParams.topMargin = 8 * cardSize;
        return labelLayoutParams;
    }

    private void rearrangeCards() {
        calcSize();
        setAllCards(cardSize, cardSize * 2);
    }

    public void moveUser1(View view) {
        if (usr1) {
            findViewById(R.id.username1).setVisibility(View.INVISIBLE);
            anset1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1moveback);
            antext1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1moveback);
            usr1 = false;
        } else {
            findViewById(R.id.username1).setVisibility(View.VISIBLE);
            anset1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1movement);
            antext1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user1movement);
            usr1 = true;
        }
        anset1.setTarget(findViewById(R.id.user1));
        antext1.setTarget(findViewById(R.id.username1));
        anset1.start();
        antext1.start();
    }

    public void moveUser2(View view) {
        if (usr2) {
            findViewById(R.id.username2).setVisibility(View.INVISIBLE);
            anset2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2moveback);
            antext2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2moveback);
            usr2 = false;
        } else {
            findViewById(R.id.username2).setVisibility(View.VISIBLE);
            anset2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2movement);
            antext2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user2movement);
            usr2 = true;
        }
        anset2.setTarget(findViewById(R.id.user2));
        antext2.setTarget(findViewById(R.id.username2));
        anset2.start();
        antext2.start();
    }

    public void moveUser3(View view) {
        if (usr3) {
            findViewById(R.id.username3).setVisibility(View.INVISIBLE);
            anset3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user3moveback);
            antext3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text3moveback);
            usr3 = false;
        } else {
            findViewById(R.id.username3).setVisibility(View.VISIBLE);
            anset3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.user3movement);
            antext3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.text3movement);
            usr3 = true;
        }

        anset3.setTarget(findViewById(R.id.user3));
        antext3.setTarget(findViewById(R.id.username3));
        anset3.start();
        antext3.start();
    }

    private void calcSize() {
        this.getWindowManager().getDefaultDisplay().getSize(po);

        if (cardSize == 0) {
            sizeW = po.x / (3 * cardsInHand.size() + 1);
            cardSize = 2 * sizeW;
        } else {
            sizeW = ((po.x - (cardsInHand.size() * cardSize)) / (cardsInHand.size() + 1));
        }
        sizeH = po.y;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){
            findViewById(R.id.association).setVisibility(View.INVISIBLE);
        }

        return false;
    }

    private class OnClose extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            JSONObject jobj = new JSONObject();
            try {
                jobj.put(Constants.ROOM_FIELD, UserData.getInstance().getCurrRoom(context));
                jobj.put(Constants.NAME_FIELD, UserData.getInstance().getNickName(context));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Requests.getInstance().doPost(Constants.REMOVE_PLAYER, jobj);

            return null;
        }
    }
    private void registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(QuickstartPreferences.ROOM_MESSAGE_RECEIVED));
    }

    public void setAllPlayers(){
        for (Player p:GameState.getGame().players){

        }
    }

    public boolean amITheTeller(){
//        return GameState.getGame().currentStoryTeller.index == UserData.getInstance().getMyIndex();

        // TODO: erase!!!
        return true;
    }

    private void setTellerPic(){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) GameState.getGame().currentStoryTeller.userPic.getLayoutParams();

        if(!amITheTeller()){
            lp.leftMargin = 100;
            lp.leftMargin = 350;
        }
        findViewById(R.id.teller).setLayoutParams(lp);

    }


}


