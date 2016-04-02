package com.example.tal.myfirstapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gront on 24/03/2016.
 */
public class Game {
    List<Player> players;
    Map<Player,Integer> votes;
    Map<Player,Integer> pickedCards;
    Player currentStoryTeller;
    String currentAssociation;
    int currentWinningCard;
    List<Player> winners;
    GameState gameState = GameState.INIT_GAME;

    private static Game game;

    private Game(){
        players = new ArrayList<>();
        votes = new HashMap<>();
        pickedCards = new HashMap<>();
        winners = new ArrayList<>();
    }

    public static Game getGame(){
        return game;
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public void calculateScore(){
        handleCorrectAnswers();
        handleWrongAnswers();
        findWinners();
    }

    private void findWinners() {
        for (Player p: players){
            if (p.score > Constants.WINNING_SCORE_THRESHOLD){
                winners.add(p);
            }
        }
    }

    private void handleWrongAnswers() {
        for (Player p: votes.keySet()){
            int vote = votes.get(p);
            if (vote != currentWinningCard){
                Player player = findPlayerByPickedCard(vote);
                player.score += 1;
            }
        }
    }

    private Player findPlayerByPickedCard(int pickedCard) {
        for (Map.Entry<Player,Integer> entry : pickedCards.entrySet()) {
            if (entry.getValue() == pickedCard) {
                return  entry.getKey();
            }
        }

        return null;
    }

    private void handleCorrectAnswers() {
        int countCorrect = 0;
        for (Player player: votes.keySet()){
            if (votes.get(player) == currentWinningCard){
                countCorrect++;
                player.score += 2;
            }
        }

        if (countCorrect == 0){
            add2PointToAllExceptTeller();
        }

        if (countCorrect != 0 && countCorrect !=Constants.NUMBER_OF_PLAYERS_IN_DIXIT - 1){
            currentStoryTeller.score += 3;
        }
    }

    private void add2PointToAllExceptTeller() {
        for (Player p: players){
            if (!p.equals(currentStoryTeller)){
                p.score += 2;
            }
        }
    }

    public void setFirstStoryTeller() {
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.index - p2.index;
            }
        });

        currentStoryTeller = players.get(0);
    }

    public void continueToNextStory(){
        int i = players.indexOf(currentStoryTeller);
        currentStoryTeller = players.get((i+1)% Constants.NUMBER_OF_PLAYERS_IN_DIXIT);
        votes.clear();
        pickedCards.clear();
    }

    public void setVoteForPlayer(String playerName, int votedCard) {
        Player player = getPlayerByName(playerName);
        votes.put(player, votedCard);
    }

    public Player getPlayerByName(String name) {
        for (Player p : players){
            if (p.name.equals(name)){
                return p;
            }
        }

        return null;
    }

    public void setPickedCardForPlayer(String playerName, int pickedCard) {
        Player player = getPlayerByName(playerName);
        pickedCards.put(player, pickedCard);
    }

    public boolean allPlayersPicked() {
        return pickedCards.size() == Constants.NUMBER_OF_PLAYERS_IN_DIXIT;
    }

    public static void initGame() {
        game = new Game();
    }

    public boolean noWinner() {
        return winners.size() == 0;
    }
}
