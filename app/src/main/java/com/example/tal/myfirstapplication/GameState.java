package com.example.tal.myfirstapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gront on 24/03/2016.
 */
public class GameState {
    private static GameState gameState;
    public List<User> allUsers;

    public static GameState getInstance(){
        if (gameState == null){
            gameState = new GameState();
        }
        return gameState;
    }

    public GameState() {
        allUsers = new ArrayList<>();
    }

    public void addPlayer(User user){
        allUsers.add(user);
    }



}
