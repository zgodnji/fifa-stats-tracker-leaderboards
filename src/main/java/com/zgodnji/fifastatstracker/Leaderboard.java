package com.zgodnji.fifastatstracker;

public class Leaderboard {

    private String id;
    private String gameId;

    public Leaderboard(String id, String gameId) {
        this.id = id;
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

}
