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
public class GameState {

    List<Player> players;
    Map<Player,Integer> votes;
    Player currentStoryTeller;
    String currentAssociation;
    int currentWinningCard;

    private static GameState game = new GameState();

    private GameState(){
        players = new ArrayList<>();
        votes = new HashMap<>();
    }

    public static GameState getGame(){
        return game;
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public void calculateScore(){
        handleCorrectAnswers();
        handleWrongAnswers();
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

    private Player findPlayerByPickedCard(int vote) {
        for (Player p : votes.keySet()){
            if (p.pickedCard == vote){
                return p;
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

        if (countCorrect != 0 && countCorrect !=Constants.NUMBER_OF_PLAYERS_IN_DIXIT - 1){
            currentStoryTeller.score += 3;
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
    }

    public void setVoteForPlayer(String playerName, int votedCard) {
        Player player = getPlayerByName(playerName);
        votes.put(player, votedCard);
    }

    private Player getPlayerByName(String name) {
        for (Player p : players){
            if (p.name.equals(name)){
                return p;
            }
        }

        return null;
    }
    public Player getPlayerByIndex(int index) {
        for (Player p : players){
            if (p.index == index){
                return p;
            }
        }

        return null;
    }
}
