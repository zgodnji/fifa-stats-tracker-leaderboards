package com.zgodnji.fifastatstracker;

import java.util.ArrayList;
import java.util.List;


public class Database {
    private static List<Leaderboard> leaderboards = new ArrayList<>();

    public static List<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public static Leaderboard getLeaderboard(String leaserboardId) {
        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard.getId().equals(leaserboardId))
                return leaderboard;
        }

        return null;
    }

    public static void addLeaderboard(Leaderboard leaderboard) {
        leaderboards.add(leaderboard);
    }

    public static void deleteLeaderboard(String leaserboardId) {
        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard.getId().equals(leaserboardId)) {
                leaderboards.remove(leaderboard);
                break;
            }
        }
    }
}
