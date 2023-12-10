package com.example.newgame;

public interface GameObserver {//Observer design pattern
    void update(int score, boolean isEnd, String playerName, int bestRecord, String bestPlayerName);
}
