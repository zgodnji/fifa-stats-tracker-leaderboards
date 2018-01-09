package com.zgodnji.fifastatstracker;

import java.util.ArrayList;
import java.util.List;


public class Database {
    private static List<Game> games = new ArrayList<>();

    public static List<Game> getGames() {
        return games;
    }

    public static Game getGame(String gameId) {
        for (Game game : games) {
            if (game.getId().equals(gameId))
                return game;
        }

        return null;
    }

    public static void addGame(Game game) {
        games.add(game);
    }

    public static void deleteGame(String gameId) {
        for (Game game : games) {
            if (game.getId().equals(gameId)) {
                games.remove(game);
                break;
            }
        }
    }
}
