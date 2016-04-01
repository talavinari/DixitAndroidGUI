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
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameMain extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener, View.OnKeyListener {


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
    AlphaAnimation flashingCardAnim;
    Boolean isFlashingCard = false;
    int draggedCardNum;
    List<Player> tmpPlayers;
    TextView opponentUserNameTextView1;
    TextView opponentUserNameTextView2;
    TextView opponentUserNameTextView3;
    ImageView opponentUserImageView1;
    ImageView opponentUserImageView2;
    ImageView opponentUserImageView3;

    EditText association;
    Map<ImageView, TextView> imageToTextViewMap = new HashMap<>();


    TextView cardText1;
    TextView cardText2;
    TextView cardText3;
    ImageView imageCardOpponentUser1;
    ImageView imageCardOpponentUser2;
    ImageView imageCardOpponentUser3;

    BroadcastReceiver googleCloudBroadcastReceiver;
    BroadcastReceiver inApplicationBroadcastReceiver;

    int myPickedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        findViews();
        initReceivers();
        draggedView = new Card();
        cardSize = 0;
        po = new Point();
        data = ClipData.newPlainText("", "");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        context = this;
        setCardsInPosition();
        tmpPlayers = new ArrayList<>();

        for (Player player : Game.getGame().players){
            if (!player.name.equals(UserData.getInstance().getNickName(this))){
                attachImageToPlayer(player);
            }
        }
    }

    private void findViews() {
        opponentUserNameTextView1 = (TextView) findViewById(R.id.username1);
        opponentUserNameTextView2 = (TextView) findViewById(R.id.username2);
        opponentUserNameTextView3 = (TextView) findViewById(R.id.username3);
        opponentUserImageView1 = (ImageView) findViewById(R.id.user1);
        opponentUserImageView2 = (ImageView) findViewById(R.id.user2);
        opponentUserImageView3 = (ImageView) findViewById(R.id.user3);
        association = (EditText) findViewById(R.id.association);
        cardText1 = (TextView) findViewById(R.id.user1cardtext);
        cardText2 = (TextView) findViewById(R.id.user2cardtext);
        cardText3 = (TextView) findViewById(R.id.user3cardtext);
        imageCardOpponentUser1 = (ImageView) findViewById(R.id.user1card);
        imageCardOpponentUser2 = (ImageView) findViewById(R.id.user2card);
        imageCardOpponentUser3 = (ImageView) findViewById(R.id.user3card);

        imageToTextViewMap = new HashMap<>();
        imageToTextViewMap.put(imageCardOpponentUser1, cardText1);
        imageToTextViewMap.put(imageCardOpponentUser2, cardText2);
        imageToTextViewMap.put(imageCardOpponentUser3, cardText3);
    }

    private void initReceivers() {
        initGCMReceiver();
        initInAppReceiver();
    }

    private void initInAppReceiver() {
        inApplicationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageType = intent.getExtras().getString(Constants.IN_APP_MESSAGE_TYPE);
                if (messageType!=null && messageType.equals(Constants.CARD_RECEIVED_EVENT)){
                    rearrangeCards();
                }
            }
        };
        registerInAppReceiver();
    }

    private void initGCMReceiver() {
        googleCloudBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle data = intent.getBundleExtra("message");
                String messageType = data.getString(Constants.MESSAGE_TYPE);
                if (MessageType.Association.getDescription().equals(messageType)) {
                    notifyAssociation(data);
                } else if (MessageType.Vote.getDescription().equals(messageType)) {
                    notifyVote(data);
                } else if (MessageType.JoinedToRoom.getDescription().equals(messageType)) {
                    notifyJoinedToRoom(data);
                } else if (MessageType.PickedCard.getDescription().equals(messageType)) {
                    notifyPlayerPickedCard(data);
                }
            }
        };
        registerGCMReceiver();
    }

    private void notifyPlayerPickedCard(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        if (checkNotSelfNotification(playerName)) {
            int pickedCard = Integer.parseInt(data.getString(Constants.WINNING_CARD));
            Game.getGame().setPickedCardForPlayer(playerName, pickedCard);
            handleAfterAllPickedCrads();
        }
    }

    private void handleAfterAllPickedCrads() {
        if (Game.getGame().allPlayersPicked()) {
            Game.getGame().gameState = GameState.VOTING;
            handlePickedCardsGUI();
        }
    }

    private boolean checkNotSelfNotification(String playerName) {
        return !playerName.equals(UserData.getInstance().getNickName(context));
    }

    private void notifyVote(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        if (checkNotSelfNotification(playerName)) {
            int votedCard = Integer.valueOf(data.getString(Constants.VOTED_CARD));
            Game.getGame().setVoteForPlayer(playerName, votedCard);
            handleAfterAllVotes();
        }
    }

    private void handleAfterAllVotes() {
        Game game = Game.getGame();
        if (game.votes.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT - 1) {
            game.calculateScore();
            if (game.noWinner()) {
                game.continueToNextStory();
            }
            else{
                handleWinningGUI();
                return;
            }
            Game.getGame().gameState = GameState.WAITING_FOR_ASSOCIATION;
            // TODO -- after 2 second delay - for show the winner and score
            updateGUI();
        }
    }

    private void handleWinningGUI() {
        // TODO winnig GUI - Unsubscribe from topic + cant touch anything
        List<Player> winners = Game.getGame().winners;


    }

    private void notifyAssociation(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        if (checkNotSelfNotification(playerName)) {
            int pickedWinner = Integer.valueOf(data.getString(Constants.WINNING_CARD));
            Game.getGame().currentWinningCard = pickedWinner;
            Game.getGame().currentAssociation = data.getString(Constants.ASSOCIATION);
            Game.getGame().setPickedCardForPlayer(playerName, pickedWinner);

            Game.getGame().gameState = GameState.PICKING_CARDS;
        }
        handleAssociationGUI();
    }

    private void handleAssociationGUI() {
        // TODO handle association received GUI

        association.setText(Game.getGame().currentAssociation);
        association.setVisibility(View.VISIBLE);
        association.setKeyListener(null);


        if (!amITheTeller()){
            isCardOnTable = false;
        }
    }


    private Collection<Integer> getOtherCardsPicked(){
        Collection<Integer> values = Game.getGame().pickedCards.values();
        values.remove(myPickedCard);
        return  values;
    }

    private void handlePickedCardsGUI() {
        List<Integer> values = new ArrayList<>(getOtherCardsPicked());

        Collections.shuffle(values);

        cardText1.setText(String.valueOf(values.get(0)));
        cardText2.setText(String.valueOf(values.get(1)));
        cardText3.setText(String.valueOf(values.get(2)));

        cardText1.setVisibility(View.VISIBLE);
        cardText2.setVisibility(View.VISIBLE);
        cardText3.setVisibility(View.VISIBLE);

        imageCardOpponentUser1.setVisibility(View.VISIBLE);
        imageCardOpponentUser2.setVisibility(View.VISIBLE);
        imageCardOpponentUser3.setVisibility(View.VISIBLE);

        imageCardOpponentUser1.setOnLongClickListener(this);
        imageCardOpponentUser2.setOnLongClickListener(this);
        imageCardOpponentUser3.setOnLongClickListener(this);

    }

    private void updateGUI() {
        //TODO gui of next trun
        //setTellerPic();
        // TODO get card.

        findViewById(R.id.user1card).setVisibility(View.INVISIBLE);
        findViewById(R.id.user2card).setVisibility(View.INVISIBLE);
        findViewById(R.id.user3card).setVisibility(View.INVISIBLE);

    }

    private void notifyJoinedToRoom(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);

        // Excluding self notification
        if (checkNotSelfNotification(playerName)) {
            int index = Integer.valueOf(data.getString(Constants.INDEX));
            Game.getGame().addPlayer(attachImageToPlayer(new Player(playerName, index)));
        }

        if (Game.getGame().players.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT) {
            startGameGUI();
            Game.getGame().gameState = GameState.WAITING_FOR_ASSOCIATION;
        }
    }

    private void startGameGUI() {
        // TODO handle GUI of start
        Game.getGame().setFirstStoryTeller();
        //setTellerPic();
    }

    @Override
    protected void onDestroy() {
        new OnClose(this).execute();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (Game.getGame().gameState){
            case VOTING:
                if (isOneOfOpponentsCards(v) && v.getAnimation() != null){
                    flashingCardAnim.cancel();
                    isFlashingCard = false;
                    String votedCard = imageToTextViewMap.get(v).getText().toString();
                    Game.getGame().setVoteForPlayer(UserData.getInstance().getNickName(context), Integer.valueOf(votedCard));
                    handleAfterAllVotes();
                    new VoteTask(this).execute(votedCard);
                }
                break;
            case PICKING_CARDS:
                if (isOneOfOpponentsCards(v) && !isFlashingCard){
                    if (v.getLayoutParams().height < cardSize * 5){
                        setBigCard(v);
                    }else{
                        setRegularCard(v);
                    }
                }

                break;

            case WAITING_FOR_ASSOCIATION:
                if (getListPlaceByView(v) != -1) {
                    int i = getListPlaceByView(v);
                    if (v.getLayoutParams().height < cardSize * 5 && i >= 0) {
                        setBigCardLayout(getListPlaceByView(v));
                    } else {
                        setAllCards(cardSize, cardSize * 2);
                    }
                } else if (
                        v == findViewById(R.id.user1card) ||
                                v == findViewById(R.id.user2card) ||
                                v == findViewById(R.id.user3card)) {
                    if (v.getLayoutParams().height < cardSize * 5){
                        setBigCard(v);
                    }else{
                        setRegularCard(v);
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        switch (Game.getGame().gameState){
            case VOTING:
                if (isOneOfOpponentsCards(v) && !amITheTeller()) {
                    setRegularCard(v);
                    v.startAnimation(flashingCardAnim);
                        //flashingCardAnim.start();
                    isFlashingCard = true;
                }
                break;
            case PICKING_CARDS:
                if (!amITheTeller() || !isCardOnTable) {
                    draggedCardNum = getListPlaceByView(v);
                    draggedView = cardsInHand.get(draggedCardNum);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    GameMain.vib.vibrate(20);
                    targetCard.setVisibility(View.VISIBLE);
                    targetCard.bringToFront();
                    return true;
                }
                break;

            case WAITING_FOR_ASSOCIATION:
//
                if (amITheTeller() || !isCardOnTable) {
                    draggedCardNum = getListPlaceByView(v);
                    draggedView = cardsInHand.get(draggedCardNum);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    GameMain.vib.vibrate(20);
                    targetCard.setVisibility(View.VISIBLE);
                    targetCard.bringToFront();
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean isOneOfOpponentsCards(View v) {
        return v.equals(imageCardOpponentUser1) ||v.equals(imageCardOpponentUser2) ||v.equals(imageCardOpponentUser3);
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
                targetCard.setVisibility(View.INVISIBLE);
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
                    String pickedCard = draggedView.tv.getText().toString();
                    myPickedCard = Integer.valueOf(pickedCard);
//                    draggedView.setVisibility(View.VISIBLE);
                    if (amITheTeller()) {
                        findViewById(R.id.association).setVisibility(View.VISIBLE);
                    }else{
                        notifySelfPicked();
                        handleAfterAllPickedCrads();
                        new PickCardTask(this).execute(pickedCard) ;
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

    private void notifySelfPicked() {
        Game.getGame().setPickedCardForPlayer(UserData.getInstance().getNickName(context), myPickedCard);
    }

    private Player attachImageToPlayer(Player player){
        if(opponentUserNameTextView1.getText().toString().equals(getString(R.string.noNameUser))){
            opponentUserNameTextView1.setText(player.name);
            player.userPic = opponentUserImageView1;
            player.username = opponentUserNameTextView1;
            player.setVisibility(View.VISIBLE);
        }else if(opponentUserNameTextView2.getText().toString().equals(getString(R.string.noNameUser))){
            opponentUserNameTextView2.setText(player.name);
            player.userPic = opponentUserImageView2;
            player.username = opponentUserNameTextView2;
            player.setVisibility(View.VISIBLE);
        }else if(opponentUserNameTextView3.getText().toString().equals(getString(R.string.noNameUser))){
            opponentUserNameTextView3.setText(player.name);
            player.userPic = opponentUserImageView3;
            player.username = opponentUserNameTextView3;
            player.setVisibility(View.VISIBLE);
        }
        player.setVisibility(View.VISIBLE);
        return player;
    }
    public void setCardsInPosition() {

        handleCards();
        calcSize();

        findViewById(R.id.user1card).setOnClickListener(this);
        findViewById(R.id.user2card).setOnClickListener(this);
        findViewById(R.id.user3card).setOnClickListener(this);

        findViewById(R.id.teller).setVisibility(View.INVISIBLE);

        findViewById(R.id.user1).setVisibility(View.INVISIBLE);
        findViewById(R.id.user2).setVisibility(View.INVISIBLE);
        findViewById(R.id.user3).setVisibility(View.INVISIBLE);
        findViewById(R.id.username1).setVisibility(View.INVISIBLE);
        findViewById(R.id.username2).setVisibility(View.INVISIBLE);
        findViewById(R.id.username3).setVisibility(View.INVISIBLE);

//        flashingCardAnim = new AlphaAnimation(context,Xml.asAttributeSet(getResources().getXml(R.animator.flashingcard)));
        flashingCardAnim = new AlphaAnimation(1.0f,0.2f);
        flashingCardAnim.setDuration(300);
        flashingCardAnim.setRepeatMode(AlphaAnimation.REVERSE);
        flashingCardAnim.setRepeatCount(AlphaAnimation.INFINITE);

        findViewById(R.id.user1card).setVisibility(View.INVISIBLE);
        findViewById(R.id.user2card).setVisibility(View.INVISIBLE);
        findViewById(R.id.user3card).setVisibility(View.INVISIBLE);
        findViewById(R.id.user1cardtext).setVisibility(View.INVISIBLE);
        findViewById(R.id.user2cardtext).setVisibility(View.INVISIBLE);
        findViewById(R.id.user3cardtext).setVisibility(View.INVISIBLE);


//        findViewById(R.id.user1card).setVisibility(View.INVISIBLE);
//        findViewById(R.id.user2card).setVisibility(View.INVISIBLE);
//        findViewById(R.id.user3card).setVisibility(View.INVISIBLE);

        setRegularCard(findViewById(R.id.user1card));
        setRegularCard(findViewById(R.id.user2card));
       // setRegularCard(findViewById(R.id.user3card));

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
                UserData.getInstance().getCards().get(0)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1),
                (ImageView) findViewById(R.id.card2), this,
                (TextView) findViewById(R.id.card2text),
                UserData.getInstance().getCards().get(1)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card3), this, (TextView) findViewById(R.id.card3text), UserData.getInstance().getCards().get(2)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card4), this, (TextView) findViewById(R.id.card4text), UserData.getInstance().getCards().get(3)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card5), this, (TextView) findViewById(R.id.card5text), UserData.getInstance().getCards().get(4)));
        cardsInHand.add(new Card(1, 1, new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card6), this, (TextView) findViewById(R.id.card6text), UserData.getInstance().getCards().get(5)));
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

    private void setRegularCard(View view){
        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        labelLayoutParams.width = cardSize;
        labelLayoutParams.height = cardSize * 2;
        view.setLayoutParams(labelLayoutParams);
    }


    private void setBigCard(View view){
        int bigCardSize = cardSize * 5;
        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        labelLayoutParams.width = bigCardSize;
        labelLayoutParams.height = bigCardSize * 2;
        view.setLayoutParams(labelLayoutParams);
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
//
//        for (Card card : cardsInHand){
//            for (String str :UserData.getInstance().getCards()){
//                if (!String.valueOf(card.imageNum).equals(str)){
//                    // TODO might be wrong -- maybe need to remove all cards.
//                    cardsInHand.add(new Card(cardsInHand.size(),Integer.parseInt(str),new RelativeLayout.LayoutParams(1, 1), (ImageView) findViewById(R.id.card6), this, (TextView) findViewById(R.id.card6text), str));
//                }
//            }
//        }

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
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            EditText associationEditText = (EditText)findViewById(R.id.association);
            associationEditText.setVisibility(View.INVISIBLE);
            String association = associationEditText.getText().toString();
            Game.getGame().currentAssociation = association;
            Game.getGame().currentWinningCard = myPickedCard;
            new SendAssociationTask(context).execute(String.valueOf(myPickedCard),
                    association);
            notifySelfPicked();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            Game.getGame().gameState = GameState.PICKING_CARDS;
        }

        return false;
    }

    private class OnClose extends BaseTask {

        public OnClose(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Requests.doPost(Constants.REMOVE_PLAYER, getBasicInfoJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void registerGCMReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(googleCloudBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.ROOM_MESSAGE_RECEIVED));
    }
    private void registerInAppReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(inApplicationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.IN_APP_MESSAGE));
    }


    private void setTellerPic() {
        if(!amITheTeller()) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) Game.getGame().currentStoryTeller.userPic.getLayoutParams();
//
//            if(Game.getGame().currentStoryTeller.userPic.getX() - 200 > po.x/2){
//
//                lp.leftMargin = (int) Game.getGame().currentStoryTeller.userPic.getX();
//            }else{
//                lp.leftMargin = (int) Game.getGame().currentStoryTeller.userPic.getX() + 300;
//            }
//            lp.bottomMargin = (int) Game.getGame().currentStoryTeller.userPic.getY();
//            findViewById(R.id.teller).setLayoutParams(lp);
        }else{
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.teller).getLayoutParams();
//            lp.leftMargin = (int) (po.x - (lp.width * 1.5));
//            lp.bottomMargin = (po.y - findViewById(R.id.table).getHeight());

        }

    }

    private boolean amITheTeller() {
        return Game.getGame().currentStoryTeller.name.equals(UserData.getInstance().getNickName(this));
    }
}


