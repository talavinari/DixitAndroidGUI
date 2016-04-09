package com.example.tal.myfirstapplication;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMain extends Activity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    MediaPlayer soundWinner;
    MediaPlayer soundLoser;
    MediaPlayer soundRoundEnd;

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

    AlphaAnimation flashingCardAnim;
    Boolean isFlashingCard = false;
    int draggedCardNum;
    boolean isTyping;
    TextView opponentUserNameTextView1;
    TextView opponentUserNameTextView2;
    TextView opponentUserNameTextView3;
    TextView endGame;

    ImageView opponentUserImageView1;
    ImageView opponentUserImageView2;
    ImageView opponentUserImageView3;

    Boolean isVoted;
    ImageView cardImage1;
    ImageView cardImage2;
    ImageView cardImage3;
    ImageView cardImage4;
    ImageView cardImage5;
    ImageView cardImage6;

    TextView card1TextView;
    TextView card2TextView;
    TextView card3TextView;
    TextView card4TextView;
    TextView card5TextView;
    TextView card6TextView;

    ImageView picked;

    List<ImageView> cardsImages;

    ImageView tableImage;
    EditText association;
    Map<ImageView, TextView> imageToTextViewMap = new HashMap<>();
    Map <Integer,Point> cardsInHandParams = new HashMap<>();
    ImageView teller;
    TextView cardText1;
    TextView cardText2;
    TextView cardText3;

    ImageView imageCardOpponentUser1;
    ImageView imageCardOpponentUser3;
    ImageView imageCardOpponentUser2;
    ImageView flashingCard;
    ImageView transSheet;

    ImageView crown;

    BroadcastReceiver googleCloudBroadcastReceiver;

    TextView myScore;
    TextView scorePlayer1;
    TextView scorePlayer2;

    ImageView associationButton;
    TextView scorePlayer3;
    boolean usr1 = false;
    boolean usr2 = false;
    boolean usr3 = false;

    boolean isGameStarted = false;

    // Property animation
    int user1ShowX;
    int user2ShowX;
    int user3ShowY;

    int user1HideX;
    int user2HideX;
    int user3HideY;

    int myPickedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_main);
        findViews();
        initListeners();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        ActionBar ac = getActionBar();
        if (ac != null) {
            ac.hide();
        }
        initGCMReceiver();

        draggedView = new Card();
        cardSize = 0;
        po = new Point();
        data = ClipData.newPlainText("", "");
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        context = this;

        setCardsInPosition();

        for (Player player : Game.getGame().players) {
            if (!player.name.equals(UserData.getInstance().getNickName(this))) {
                attachImageToPlayer(player);
            }
        }

        if (Game.getGame().players.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT) {
            Game.getGame().gameState = GameState.WAITING_FOR_ASSOCIATION;
        }

    }

    private void initListeners() {
        imageCardOpponentUser1.setOnLongClickListener(this);
        imageCardOpponentUser2.setOnLongClickListener(this);
        imageCardOpponentUser3.setOnLongClickListener(this);
        tableImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBlinking();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isGameStarted) {
            isGameStarted= true;
            startCardsAnimation();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Game.getGame().gameState.equals(GameState.INIT_GAME)) {

            startGameGUI();
            setOpponentCarsPosition();
        }
    }

    private void findViews() {
        tableImage = (ImageView)findViewById(R.id.table);
        targetCard = (ImageView) findViewById(R.id.target);
        opponentUserNameTextView1 = (TextView) findViewById(R.id.username1);
        opponentUserNameTextView2 = (TextView) findViewById(R.id.username2);
        opponentUserNameTextView3 = (TextView) findViewById(R.id.username3);
        opponentUserImageView1 = (ImageView) findViewById(R.id.user1);
        opponentUserImageView2 = (ImageView) findViewById(R.id.user2);
        opponentUserImageView3 = (ImageView) findViewById(R.id.user3);

        cardText1 = (TextView) findViewById(R.id.user1cardtext);
        cardText2 = (TextView) findViewById(R.id.user2cardtext);
        cardText3 = (TextView) findViewById(R.id.user3cardtext);
        imageCardOpponentUser1 = (ImageView) findViewById(R.id.user1card);
        imageCardOpponentUser2 = (ImageView) findViewById(R.id.user2card);
        imageCardOpponentUser3 = (ImageView) findViewById(R.id.user3card);

        associationButton = (ImageView) findViewById(R.id.association_button);

        teller = (ImageView) findViewById(R.id.teller);
        teller.getLayoutParams().height=150;
        teller.getLayoutParams().width=150;
        picked = (ImageView) findViewById(R.id.picked);

        soundWinner = MediaPlayer.create(this, R.raw.round_end);
        soundLoser = MediaPlayer.create(this, R.raw.loser);
        soundRoundEnd = MediaPlayer.create(this, R.raw.round_winner);

        crown = (ImageView) findViewById(R.id.crown);
        transSheet = (ImageView) findViewById(R.id.transSheet);
        endGame = (TextView) findViewById(R.id.endGame);

        imageToTextViewMap = new HashMap<>();
        imageToTextViewMap.put(imageCardOpponentUser1, cardText1);
        imageToTextViewMap.put(imageCardOpponentUser2, cardText2);
        imageToTextViewMap.put(imageCardOpponentUser3, cardText3);

        cardImage1 = (ImageView) findViewById(R.id.card1);
        cardImage2 = (ImageView) findViewById(R.id.card2);
        cardImage3 = (ImageView) findViewById(R.id.card3);
        cardImage4 = (ImageView) findViewById(R.id.card4);
        cardImage5 = (ImageView) findViewById(R.id.card5);
        cardImage6 = (ImageView) findViewById(R.id.card6);
        cardsImages = new ArrayList<>(Arrays.asList(new ImageView[]{cardImage1, cardImage2, cardImage3,
                cardImage4, cardImage5, cardImage6}));

        card1TextView = (TextView) findViewById(R.id.card1text);
        card2TextView = (TextView) findViewById(R.id.card2text);
        card3TextView = (TextView) findViewById(R.id.card3text);
        card4TextView = (TextView) findViewById(R.id.card4text);
        card5TextView = (TextView) findViewById(R.id.card5text);
        card6TextView = (TextView) findViewById(R.id.card6text);


        myScore = ((TextView) findViewById(R.id.myscore));
        scorePlayer1 = ((TextView) findViewById(R.id.score1));
        scorePlayer2 = ((TextView) findViewById(R.id.score2));
        scorePlayer3 = ((TextView) findViewById(R.id.score3));

        association = (EditText) findViewById(R.id.association);

        association.setHintTextColor(Color.DKGRAY);
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
            handleAfterAllPickedCards();
        }
    }

    private void handleAfterAllPickedCards() {
        if (Game.getGame().allPlayersPicked()) {
            Game.getGame().gameState = GameState.VOTING;
            isVoted = false;
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
            handleScoreLabels();
            associationButton.setVisibility(View.INVISIBLE);
            association.setVisibility(View.INVISIBLE);
            if (game.noWinner()) {
                game.continueToNextStory();
            } else {
                handleWinningGUI();
                return;
            }
            Game.getGame().gameState = GameState.WAITING_FOR_ASSOCIATION;
            updateAfterRoundGUI();
        }
    }

    private void handleScoreLabels() {
        myScore.setText(getPlayerScoreString(UserData.getInstance().getNickName(this), true));
        scorePlayer1.setText(getPlayerScoreString(opponentUserNameTextView1.getText().toString()));
        scorePlayer2.setText(getPlayerScoreString(opponentUserNameTextView2.getText().toString()));
        scorePlayer3.setText(getPlayerScoreString(opponentUserNameTextView3.getText().toString()));
    }

    @NonNull
    private String getPlayerScoreString(String playerName) {
        return getPlayerScoreString(playerName, false);
    }

    private String getPlayerScoreString(String playerName, boolean isCurrentPlayer) {
        Player p = Game.getGame().getPlayerByName(playerName);
        return (isCurrentPlayer ? getString(R.string.myScore) : getString(R.string.score))
                + p.score;
    }

    private void handleWinningGUI() {
        // TODO winning GUI - Unsubscribe from topic + cant touch anything
        List<Player> winners = Game.getGame().winners;

        usr1 = false;
        usr2 = false;
        usr3 = false;



        if(winners.contains(Game.getGame().getPlayerByName(UserData.getInstance().getNickName(this)))){
            soundWinner.start();
        }else{
            soundLoser.start();
        }

        moveUsers();
        TranslateAnimation crownAnimation = new TranslateAnimation(winners.get(0).userPic.getX(),winners.get(0).userPic.getX(),-winners.get(0).userPic.getHeight(),winners.get(0).userPic.getY()-(winners.get(0).userPic.getHeight()*2/3));
        picked.setVisibility(View.INVISIBLE);
        crown.setVisibility(View.VISIBLE);

        crownAnimation.setDuration(3500);
        crownAnimation.setFillAfter(true);
        crown.startAnimation(crownAnimation);

        Game.getGame().gameState = GameState.GAME_ENDED;

        transSheet.setVisibility(View.VISIBLE);
        transSheet.bringToFront();
        transSheet.animate().alpha((float) 0.5).start();
        endGame.setVisibility(View.VISIBLE);
        endGame.bringToFront();
        endGame.animate().alpha((float) 0.5).start();

        unregisterFromTopic();

        transSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChooseCreateJoin.class);
                startActivity(intent);
            }
        });
    }

    private void unregisterFromTopic() {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.putExtra(Constants.OPERATION_TYPE, Constants.UNREGISTER_OPERATION);
        intent.putExtra(Constants.TOPIC_ROOM_NAME, UserData.getInstance().getCurrRoom(this));
        startService(intent);
    }

    private void notifyAssociation(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);
        if (checkNotSelfNotification(playerName)) {
            int pickedWinner = Integer.valueOf(data.getString(Constants.WINNING_CARD));
            Game.getGame().currentWinningCard = pickedWinner;
            String associationText;
            try {
                associationText = URLDecoder.decode(data.getString(Constants.ASSOCIATION), "UTF8");
            } catch (UnsupportedEncodingException e) {
                associationText = data.getString(Constants.ASSOCIATION);
            }
            Game.getGame().currentAssociation = associationText;
            Game.getGame().setPickedCardForPlayer(playerName, pickedWinner);

            if (isAssociationHidden()){
                hideAndShowAssociation(association);
            }

            Game.getGame().gameState = GameState.PICKING_CARDS;
        }
        handleAssociationGUI(false);
    }

    private boolean isAssociationHidden(){
        if (association.getX() < 0){
            return true;
        }
        return false;
    }

    private void handleAssociationGUI(boolean editable) {
        association.setText(Game.getGame().currentAssociation);
        association.setVisibility(View.VISIBLE);
        associationButton.setVisibility(View.VISIBLE);
        association.setFocusableInTouchMode(editable);
        association.setFocusable(editable);
        association.setClickable(editable);
        isTyping = editable;
        if (isAssociationHidden() && editable){
            hideAndShowAssociation(association);
        }
        if (!amITheTeller()) {
            isCardOnTable = false;
        }
    }

    private void handlePickedCardsGUI() {
        List<Integer> values = new ArrayList<>();
        for (Integer picked : Game.getGame().pickedCards.values()) {
            if (picked != myPickedCard) {
                values.add(picked);
            }
        }
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

        imageCardOpponentUser1.setImageDrawable(getImageByCardNumber(String.valueOf(values.get(0))));
        imageCardOpponentUser2.setImageDrawable(getImageByCardNumber(String.valueOf(values.get(1))));
        imageCardOpponentUser3.setImageDrawable(getImageByCardNumber(String.valueOf(values.get(2))));


    }
    private void userAndScoresPresentation(){
        picked.setVisibility(View.INVISIBLE);
        usr1 = false;
        usr2 = false;
        usr3 = false;
        moveUsersBackAndForth(2500);

    }

    private void moveUsers(){
        moveUser1(null);
        moveUser2(null);
        moveUser3(null);
    }

    private void moveUsersBackAndForth(int delay) {
        scorePlayer1.setVisibility(View.VISIBLE);

        opponentUserImageView1.animate().x(user1ShowX).setDuration(300);
        opponentUserNameTextView1.animate().x(user1ShowX).setDuration(300);
        scorePlayer1.animate().x(user1ShowX).setDuration(300);
        opponentUserImageView2.animate().x(user2ShowX).setDuration(300);
        opponentUserNameTextView2.animate().x(user2ShowX).setDuration(300);
        scorePlayer2.animate().x(user2ShowX).setDuration(300);
        opponentUserImageView3.animate().y(user3ShowY).setDuration(300);
        opponentUserNameTextView3.animate().y(user3ShowY).setDuration(300);
        scorePlayer3.animate().y(user3ShowY).setDuration(300);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        opponentUserImageView1.animate().x(user1HideX).setDuration(300);
        opponentUserNameTextView1.animate().x(user1HideX).setDuration(300);
        scorePlayer1.animate().x(user1HideX).setDuration(300);
        opponentUserImageView2.animate().x(user2HideX).setDuration(300);
        opponentUserNameTextView2.animate().x(user2HideX).setDuration(300);
        scorePlayer2.animate().x(user2HideX).setDuration(300);
        opponentUserImageView3.animate().y(user3HideY).setDuration(300);
        opponentUserNameTextView3.animate().y(user3HideY).setDuration(300);
        scorePlayer3.animate().y(user3HideY).setDuration(300);
    }

    private void updateAfterRoundGUI() {
        cardsInHand.clear();
        soundRoundEnd.start();
        userAndScoresPresentation();
        setTellerPic();
        handleCards();
        rearrangeCards();
        setTargetCardEmpty();
        isCardOnTable = false;
        setOpponentsCardVisibility(View.GONE);
    }

    private void setOpponentsCardVisibility(int visibility) {
        imageCardOpponentUser1.setVisibility(visibility);
        imageCardOpponentUser2.setVisibility(visibility);
        imageCardOpponentUser3.setVisibility(visibility);
        cardText1.setVisibility(visibility);
        cardText2.setVisibility(visibility);
        cardText3.setVisibility(visibility);
    }

    private void notifyJoinedToRoom(Bundle data) {
        String playerName = data.getString(Constants.PLAYER_NAME);

        // Excluding self notification
        if (checkNotSelfNotification(playerName)) {
            int index = Integer.valueOf(data.getString(Constants.INDEX));
            Game.getGame().addPlayer(attachImageToPlayer(new Player(playerName, index)));

            if (Game.getGame().players.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT) {
                Game.getGame().gameState = GameState.WAITING_FOR_ASSOCIATION;
            }
        }
    }

    private void startGameGUI() {
        Game.getGame().setFirstStoryTeller();
        setPropertyAnimation();
        setTellerPic();
    }

    @Override
    protected void onDestroy() {
        Game.initGame();
        new OnClose(this).execute();
        super.onDestroy();
    }

    private void setPickedPlace(View view){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) picked.getLayoutParams();
        lp.leftMargin = (int) (view.getX() + view.getWidth() - (picked.getWidth()/2));
        lp.topMargin = (int) (view.getY() - (picked.getHeight()/2));
        picked.setLayoutParams(lp);
        picked.setVisibility(View.VISIBLE);
        picked.bringToFront();
    }

    private void setBigOpponentCard(View v){

        int bigCardSize = cardSize * 5;
        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        labelLayoutParams.width = bigCardSize;
        labelLayoutParams.height = (int) (bigCardSize * 1.5);
        labelLayoutParams.leftMargin = (po.x - (cardSize * 5))/2;
        v.setLayoutParams(labelLayoutParams);
    }

    private void setOpponentRegularCard(View v){

        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        labelLayoutParams.width = cardSize;
        labelLayoutParams.height = (int) (cardSize * 1.5);
        if (v.equals(imageCardOpponentUser1)){
            labelLayoutParams.leftMargin = po.x/2 -opponentUserImageView1.getWidth();

        }else if(v.equals(imageCardOpponentUser2)){
            labelLayoutParams.leftMargin = po.x/2 +opponentUserImageView1.getWidth() - cardSize;

        }else if(v.equals(imageCardOpponentUser3)){
            labelLayoutParams.leftMargin = (po.x -cardSize)/2;
        }

        v.setLayoutParams(labelLayoutParams);
    }

    private void setAllOppCardsRegular(){
        setOpponentRegularCard(imageCardOpponentUser1);
        setOpponentRegularCard(imageCardOpponentUser2);
        setOpponentRegularCard(imageCardOpponentUser3);
    }

    @Override
    public void onClick(View v) {
        switch (Game.getGame().gameState) {
            case VOTING:
                if (isOneOfOpponentsCards(v)) {
                    if ((isFlashingCard) && (!isVoted) && !amITheTeller() && v.equals(flashingCard) ){
                        String votedCard = imageToTextViewMap.get(v).getText().toString();
                        Game.getGame().setVoteForPlayer(UserData.getInstance().getNickName(context), Integer.valueOf(votedCard));
                        new VoteTask(this).execute(votedCard);
                        isVoted = true;
                        handleAfterAllVotes();
                        setPickedPlace(v);
                    } else {
                        if (v.getLayoutParams().height > cardSize * 1.5) {
                            setOpponentRegularCard(v);
                        } else {
                            setAllOppCardsRegular();
                            setBigOpponentCard(v);
                            v.bringToFront();
                            picked.bringToFront();
                        }
                    }
                }

                stopBlinking();

                break;
            case PICKING_CARDS:
                if (getListPlaceByView(v) != -1) {
                    int i = getListPlaceByView(v);
                    if (v.getLayoutParams().height < cardSize * 5 && i >= 0) {
                        setBigCardLayout(getListPlaceByView(v));
                    } else {
                        setAllCards(cardSize, (int) (cardSize * 1.5));
                    }
                }
                break;

            case WAITING_FOR_ASSOCIATION:
                if (getListPlaceByView(v) != -1) {
                    int i = getListPlaceByView(v);
                    if (v.getLayoutParams().height < cardSize * 5 && i >= 0) {
                        setBigCardLayout(getListPlaceByView(v));
                    } else {
                        setAllCards(cardSize, (int) (cardSize * 1.5));
                    }
                } else if (
                        oneOfOpponentPickedCards(v)) {
                    if (v.getLayoutParams().height < cardSize * 5) {
                        setBigCard(v);
                    } else {
                        setRegularCard(v);
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean oneOfOpponentPickedCards(View v) {
        return v == imageCardOpponentUser1 ||
                v == imageCardOpponentUser2 ||
                v == imageCardOpponentUser3;
    }

    private void stopBlinking(){
        if (flashingCard != null) {
            flashingCard.clearAnimation();
        }
        isFlashingCard = false;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (Game.getGame().gameState) {
            case VOTING:
                if (isOneOfOpponentsCards(v) && !amITheTeller() && !isVoted && !isFlashingCard) {
                    v.startAnimation(flashingCardAnim);
                    isFlashingCard = true;
                    flashingCard = (ImageView) v;
                }
                break;
            case PICKING_CARDS:
                if (!amITheTeller() && !isCardOnTable) {
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
                if (amITheTeller() && !isCardOnTable) {
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
        return true;
    }

    private boolean isOneOfOpponentsCards(View v) {
        return v.equals(imageCardOpponentUser1) || v.equals(imageCardOpponentUser2) || v.equals(imageCardOpponentUser3);
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
                setAllCards(cardSize, (int) (cardSize * 1.5));
                return true;
            case DragEvent.ACTION_DROP:
                if (tableImage.equals(v)) {
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
                        Game.getGame().currentAssociation = "";
                        handleAssociationGUI(false);
                        handleAssociationGUI(true);
                    } else {
                        notifySelfPicked();
                        handleAfterAllPickedCards();
                        new PickCardTask(this).execute(pickedCard);
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

    private Player attachImageToPlayer(Player player) {
        if (opponentUserNameTextView1.getText().toString().equals(getString(R.string.noNameUser))) {
            opponentUserNameTextView1.setText(player.name);
            player.userPic = opponentUserImageView1;
            player.username = opponentUserNameTextView1;
            player.setVisibility(View.VISIBLE);
        } else if (opponentUserNameTextView2.getText().toString().equals(getString(R.string.noNameUser))) {
            opponentUserNameTextView2.setText(player.name);
            player.userPic = opponentUserImageView2;
            player.username = opponentUserNameTextView2;
            player.setVisibility(View.VISIBLE);
        } else if (opponentUserNameTextView3.getText().toString().equals(getString(R.string.noNameUser))) {
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
        setPropertyAnimation();
        imageCardOpponentUser1.setOnClickListener(this);
        imageCardOpponentUser2.setOnClickListener(this);
        imageCardOpponentUser3.setOnClickListener(this);

        opponentUserImageView1.setVisibility(View.INVISIBLE);
        opponentUserImageView2.setVisibility(View.INVISIBLE);
        opponentUserImageView3.setVisibility(View.INVISIBLE);
        opponentUserNameTextView1.setVisibility(View.INVISIBLE);
        opponentUserNameTextView2.setVisibility(View.INVISIBLE);
        opponentUserNameTextView3.setVisibility(View.INVISIBLE);

        flashingCardAnim = new AlphaAnimation(1.0f, 0.2f);
        flashingCardAnim.setDuration(500);
        flashingCardAnim.setRepeatMode(AlphaAnimation.REVERSE);
        flashingCardAnim.setRepeatCount(AlphaAnimation.INFINITE);

        tableImage.setSoundEffectsEnabled(false);

        associationButton.animate().rotation(-90).start();
        associationButton.setVisibility(View.INVISIBLE);

        setOpponentsCardVisibility(View.INVISIBLE);

        association.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAndShowAssociation(v);
            }
        });

        setRegularCard(imageCardOpponentUser1);
        setRegularCard(imageCardOpponentUser2);
        setRegularCard(imageCardOpponentUser3);


        setTargetCardEmpty();

        setAllCards(cardSize, (int) (cardSize * 1.5));
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.get(i).cardPic.setOnClickListener(this);
            cardsInHand.get(i).cardPic.setOnLongClickListener(this);
            cardsInHand.get(i).cardPic.setOnDragListener(this);
        }

        RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(tableImage.getLayoutParams().width, sizeH * 5 / 7);
        r.addRule(RelativeLayout.ALIGN_PARENT_START);
        tableImage.setLayoutParams(r);
        tableImage.setOnDragListener(this);
    }

    private void setOpponentCarsPosition() {
        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) imageCardOpponentUser1.getLayoutParams();
        lp1.topMargin = (int) (opponentUserImageView1.getHeight()*1.5);
        lp1.leftMargin = po.x/2 -opponentUserImageView1.getWidth();
        imageCardOpponentUser1.setLayoutParams(lp1);

        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) imageCardOpponentUser2.getLayoutParams();
        lp2.topMargin = (int) (opponentUserImageView1.getHeight()*1.5);
        lp2.leftMargin = po.x/2 +opponentUserImageView1.getWidth() - imageCardOpponentUser2.getWidth();
        imageCardOpponentUser2.setLayoutParams(lp2);

        RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams) imageCardOpponentUser3.getLayoutParams();
        lp3.topMargin = (int) (opponentUserImageView1.getHeight()*1.5);
        lp3.leftMargin = (po.x -imageCardOpponentUser3.getWidth())/2;
        imageCardOpponentUser3.setLayoutParams(lp3);
    }

    private void setTargetCardEmpty() {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setStroke(5, Color.parseColor("#111111"), 15, 8);
        targetCard.setBackground(shape);
        targetCard.setLayoutParams(getOutgoingCardLayoutParams());
        targetCard.setVisibility(View.INVISIBLE);
        targetCard.bringToFront();
    }

    private void setPropertyAnimation() {
        user1HideX = (int) (opponentUserImageView1.getWidth() * -0.5);
        user1ShowX = (int) (opponentUserImageView1.getWidth() * 0.5);

        user2HideX = (int) (po.x - opponentUserImageView2.getWidth() * 0.5);
        user2ShowX = (int) (po.x - opponentUserImageView2.getWidth() * 1.5);

        user3HideY = (int) (opponentUserImageView3.getHeight() * -0.5);
        user3ShowY = (int) (opponentUserImageView3.getHeight() * 0.5);
    }

    private void handleCards() {
        for (int i = 0; i < cardsImages.size(); i++) {
            cardsImages.get(i).setImageDrawable(
                    getImageByCardNumber(getCards(i)));
        }

        cardsInHand.add(createCardHelper(cardImage1, card1TextView, 0));
        cardsInHand.add(createCardHelper(cardImage2, card2TextView, 1));
        cardsInHand.add(createCardHelper(cardImage3, card3TextView, 2));
        cardsInHand.add(createCardHelper(cardImage4, card4TextView, 3));
        cardsInHand.add(createCardHelper(cardImage5, card5TextView, 4));
        cardsInHand.add(createCardHelper(cardImage6, card6TextView, 5));
    }

    @NonNull
    private Card createCardHelper(ImageView cardImage, TextView cardTextView, int cardNum) {
        return new Card(new RelativeLayout.LayoutParams(1, 1),
                cardImage, this,
                cardTextView,
                getCards(cardNum));
    }

    private String getCards(int index) {
        return UserData.getInstance().getCards().get(index);
    }

    private Drawable getImageByCardNumber(String cardNumber) {
        return getResources().getDrawable(getDrawableID(cardNumber), getApplicationContext().getTheme());
    }

    private int getDrawableID(String cardNumber) {
        return this.getResources().getIdentifier(Constants.IMG_PREFIX + cardNumber, "drawable", this.getPackageName());
    }

    private void setAllCards(int w, int h) {
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.get(i).cardPic.setLayoutParams(getLayoutParams(w, h, i));

        }
    }

    private void setCardsForAnimation(){
        // Left, Right
        RelativeLayout.LayoutParams lp;
        for (int i=0;i<cardsInHand.size();i++){
            cardsInHandParams.put(
                    i,
                    new Point(((RelativeLayout.LayoutParams)cardsInHand.get(i).cardPic.getLayoutParams()).leftMargin,
                            ((RelativeLayout.LayoutParams)cardsInHand.get(i).cardPic.getLayoutParams()).bottomMargin));
            lp = (RelativeLayout.LayoutParams) cardsInHand.get(i).cardPic.getLayoutParams();
            lp.leftMargin = po.x;
            lp.topMargin = po.y;
            cardsInHand.get(i).cardPic.setLayoutParams(lp);
        }
    }

    private void startCardsAnimation(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.cards);
        List<Point> lst = new ArrayList<>(cardsInHandParams.values());

        Collections.sort(lst, new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return lhs.x - rhs.x;
            }
        });

        TranslateAnimation card1anim = new TranslateAnimation(po.x,cardsInHand.get(0).cardPic.getX(),po.y,cardsInHand.get(0).cardPic.getX());
        TranslateAnimation card2anim = new TranslateAnimation(po.x,cardsInHand.get(1).cardPic.getX(),po.y,cardsInHand.get(1).cardPic.getX());
        TranslateAnimation card3anim = new TranslateAnimation(po.x,cardsInHand.get(2).cardPic.getX(),po.y,cardsInHand.get(2).cardPic.getX());
        TranslateAnimation card4anim = new TranslateAnimation(po.x,cardsInHand.get(3).cardPic.getX(),po.y,cardsInHand.get(3).cardPic.getX());
        TranslateAnimation card5anim = new TranslateAnimation(po.x,cardsInHand.get(4).cardPic.getX(),po.y,cardsInHand.get(4).cardPic.getX());
        TranslateAnimation card6anim = new TranslateAnimation(po.x,cardsInHand.get(5).cardPic.getX(),po.y,cardsInHand.get(5).cardPic.getX());

        card1anim.setDuration(350);
        card2anim.setDuration(350);
        card3anim.setDuration(350);
        card4anim.setDuration(350);
        card5anim.setDuration(350);
        card6anim.setDuration(350);

        card1anim.setStartOffset(500);
        card2anim.setStartOffset(550);
        card3anim.setStartOffset(600);
        card4anim.setStartOffset(650);
        card5anim.setStartOffset(700);
        card6anim.setStartOffset(750);

        mediaPlayer.start();

        cardsInHand.get(0).cardPic.startAnimation(card1anim);
        cardsInHand.get(1).cardPic.startAnimation(card2anim);
        cardsInHand.get(2).cardPic.startAnimation(card3anim);
        cardsInHand.get(3).cardPic.startAnimation(card4anim);
        cardsInHand.get(4).cardPic.startAnimation(card5anim);
        cardsInHand.get(5).cardPic.startAnimation(card6anim);

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

    private void setRegularCard(View view) {
        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        labelLayoutParams.width = cardSize;
        labelLayoutParams.height = (int) (cardSize * 1.5);
        view.setLayoutParams(labelLayoutParams);
    }


    private void setBigCard(View view) {
        int bigCardSize = cardSize * 5;
        RelativeLayout.LayoutParams labelLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        labelLayoutParams.width = bigCardSize;
        labelLayoutParams.height = (int) (bigCardSize * 1.5);
        view.setLayoutParams(labelLayoutParams);
    }

    private void setBigCardLayout(int i) {
        int tmpCardSize = cardSize / 2;
        int bigCardSize = cardSize * 5;
        int tmpWideSize = (po.x - (bigCardSize + (cardsInHand.size() * tmpCardSize))) / (cardsInHand.size() + 1);
        RelativeLayout.LayoutParams labelLayoutParams;

        for (int j = 0; j < cardsInHand.size(); j++) {
            if (j < i) {
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, (int) (tmpCardSize * 1.5));
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;
            } else if (j == i) {
                labelLayoutParams = new RelativeLayout.LayoutParams(bigCardSize, (int) (bigCardSize * 1.5));
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (j * (tmpCardSize + tmpWideSize)) + tmpWideSize;
            } else {
                labelLayoutParams = new RelativeLayout.LayoutParams(tmpCardSize, (int) (tmpCardSize * 1.5));
                labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                labelLayoutParams.bottomMargin = cardSize;
                labelLayoutParams.leftMargin = (int) (((j - 1) * (tmpCardSize + tmpWideSize)) + (1.5 * tmpWideSize) + bigCardSize);
            }
            cardsInHand.get(j).cardPic.setLayoutParams(labelLayoutParams);
        }
        cardsInHand.get(i).bringToFront();
    }

    private RelativeLayout.LayoutParams getOutgoingCardLayoutParams() {
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(cardSize, (int) (cardSize * 1.5));
        labelLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        labelLayoutParams.topMargin = 8 * cardSize;
        return labelLayoutParams;
    }

    private void rearrangeCards() {
        calcSize();
        setAllCards(cardSize, (int) (cardSize * 1.5));
    }

    public void moveUser1(View v) {
        ObjectAnimator userPicAnimation;
        ObjectAnimator userTextAnimation;
        ObjectAnimator userScoreAnimation;

        if (usr1) {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView1, "x", user1ShowX, user1HideX);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView1, "x", user1ShowX, user1HideX);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer1, "x", user1ShowX, user1HideX);
            scorePlayer1.setVisibility(View.INVISIBLE);
        } else {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView1, "x", user1HideX, user1ShowX);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView1, "x", user1HideX, user1ShowX);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer1, "x", user1HideX, user1ShowX);
            scorePlayer1.setVisibility(View.VISIBLE);

        }
        userPicAnimation.setDuration(300);
        userTextAnimation.setDuration(300);
        userScoreAnimation.setDuration(300);
        userPicAnimation.start();
        userTextAnimation.start();
        userScoreAnimation.start();
        usr1 = !usr1;
    }

    public void moveUser2(View v) {
        ObjectAnimator userPicAnimation;
        ObjectAnimator userTextAnimation;
        ObjectAnimator userScoreAnimation;

        if (usr2) {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView2, "x", user2ShowX, user2HideX);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView2, "x", user2ShowX, user2HideX);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer2, "x", user2ShowX, user2HideX);
            scorePlayer2.setVisibility(View.INVISIBLE);
        } else {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView2, "x", user2HideX, user2ShowX);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView2, "x", user2HideX, user2ShowX);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer2, "x", user2HideX, user2ShowX);
            scorePlayer2.setVisibility(View.VISIBLE);

        }
        userPicAnimation.setDuration(300);
        userTextAnimation.setDuration(300);
        userScoreAnimation.setDuration(300);
        userPicAnimation.start();
        userTextAnimation.start();
        userScoreAnimation.start();
        usr2 = !usr2;
    }

    public void moveUser3(View v) {
        ObjectAnimator userPicAnimation;
        ObjectAnimator userTextAnimation;
        ObjectAnimator userScoreAnimation;

        if (usr3) {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView3, "y", user3ShowY, user3HideY);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView3, "y", user3ShowY + opponentUserImageView3.getHeight() + 30, user3HideY + opponentUserImageView3.getHeight() + 30);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer3, "y", user3ShowY, user3HideY);
            scorePlayer3.setVisibility(View.INVISIBLE);

        } else {
            userPicAnimation = ObjectAnimator.ofFloat(opponentUserImageView3, "y", user3HideY, user3ShowY);
            userTextAnimation = ObjectAnimator.ofFloat(opponentUserNameTextView3, "y", user3HideY + opponentUserImageView3.getHeight() + 30, user3ShowY + opponentUserImageView3.getHeight() + 30);
            userScoreAnimation = ObjectAnimator.ofFloat(scorePlayer3, "y", user3HideY, user3ShowY);
            scorePlayer3.setVisibility(View.VISIBLE);
        }
        userPicAnimation.setDuration(300);
        userTextAnimation.setDuration(300);
        userScoreAnimation.setDuration(300);
        userPicAnimation.start();
        userTextAnimation.start();
        userScoreAnimation.start();
        usr3 = !usr3;
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
    public boolean dispatchKeyEvent(KeyEvent e) {
        super.dispatchKeyEvent(e);
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            association.setVisibility(View.INVISIBLE);
            associationButton.setVisibility(View.INVISIBLE);
            String associationString = association.getText().toString();
            Game.getGame().currentAssociation = associationString;
            Game.getGame().currentWinningCard = myPickedCard;
            new SendAssociationTask(context).execute(String.valueOf(myPickedCard),
                    associationString);
            notifySelfPicked();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(association.getWindowToken(), 0);
            Game.getGame().gameState = GameState.PICKING_CARDS;
        }
        return true;
    }

    private class OnClose extends BaseTask {

        public OnClose(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Requests.doPost(Constants.REMOVE_PLAYER, getBasicInfoJSON());
                UserData.getInstance().removeAllCards();
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

    private void setTellerPic() {
        teller.setVisibility(View.VISIBLE);
        PointF target = getTargetTellerPosition();
        teller.animate().x(target.x).y(target.y).setDuration(800);
    }

    private PointF getTargetTellerPosition(){
        float toX = 0;
        float  toY = 0;

        if (!amITheTeller()) {
            ImageView userTellerPic = Game.getGame().currentStoryTeller.userPic;
            if (userTellerPic.equals(opponentUserImageView1)) {
                toX = user1HideX + userTellerPic.getWidth() - (teller.getWidth()/2);
                toY  = userTellerPic.getY() + userTellerPic.getHeight() - (teller.getHeight()/2);
            } else if(userTellerPic.equals(opponentUserImageView2)){
                toX = user2HideX - (teller.getWidth()/2);
                toY  = userTellerPic.getY() + userTellerPic.getHeight() - (teller.getHeight()/2);
            }else if(userTellerPic.equals(opponentUserImageView3)){
                toX = userTellerPic.getX() + userTellerPic.getWidth() - (teller.getWidth()/2);
                toY  = (userTellerPic.getHeight()/2) - (teller.getHeight()/2);
            }
        } else {
            toX = (float) (po.x - (teller.getWidth() * 1.5));
            toY = (tableImage.getHeight() - (teller.getHeight() / 2));
        }

        return new PointF(toX,toY);
    }

    private boolean amITheTeller() {
        return Game.getGame().currentStoryTeller.name.equals(UserData.getInstance().getNickName(this));
    }

    public void hideAndShowAssociation(View view){
        if(!isTyping) {
            int threshold;
            if (view.equals(association)) {
                threshold = -10;
            } else {
                threshold = 0;
            }
            ObjectAnimator buttonAnimation;
            ObjectAnimator associationAnimation;
            // Hide
            associationButton.animate().rotation(180).start();
            if (view.getX() > threshold) {
                buttonAnimation = ObjectAnimator.ofFloat(associationButton, "x", associationButton.getX(), 0);
                associationAnimation = ObjectAnimator.ofFloat(association, "x", association.getX(), -association.getWidth());
            } else {
                buttonAnimation = ObjectAnimator.ofFloat(associationButton, "x", associationButton.getX(), po.x - associationButton.getWidth());
                associationAnimation = ObjectAnimator.ofFloat(association, "x", association.getX(), 0);
            }
            buttonAnimation.setDuration(300);
            associationAnimation.setDuration(300);
            buttonAnimation.start();
            associationAnimation.start();
        }
    }

}