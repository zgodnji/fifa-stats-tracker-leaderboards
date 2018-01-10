package com.zgodnji.fifastatstracker;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RequestScoped
@Path("leaderboards")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LeaderboardResource {

    @GET
    public Response getAllLeaderboards() {
        List<Leaderboard> leaderboards = Database.getLeaderboards();
        return Response.ok(leaderboards).build();
    }

    @GET
    @Path("{leaderboardId}")
    public Response getLeaderboard(@PathParam("leaderboardId") String leaderboardId) {
        Leaderboard leaderboard = Database.getLeaderboard(leaderboardId);
        return leaderboard != null
                ? Response.ok(leaderboard).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewLeaderboard(Leaderboard leaderboard) {
        Database.addLeaderboard(leaderboard);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{leaderboardId}")
    public Response deleteLeaderboard(@PathParam("leaderboardId") String leaderboardId) {
        Database.deleteLeaderboard(leaderboardId);
        return Response.noContent().build();
    }

    @GET
    @Path("create")
    public Response fillDatabase() {
        Database.addLeaderboard(new Leaderboard(
                "1",
                "2"
        ));
        return Response.noContent().build();
    }

    @Inject
    private LeaderboardProperties properties;

    @GET
    @Path("config")
    public Response getConfig() {
        String response =
                "{" +
                        "\"stringProperty\": \"%s\"," +
                        "\"booleanProperty\": %b," +
                        "\"integerProperty\": %d" +
                        "}";

        response = String.format(
                response,
                properties.getStringProperty(),
                properties.getBooleanProperty(),
                properties.getIntegerProperty());

        return Response.ok(response).build();
    }


    /*@Inject
    @DiscoverService(value = "scores-service", environment = "dev", version = "1.0.0")
    private URL url;

    @Inject
    @DiscoverService(value = "scores-service", environment = "dev", version = "1.0.0")
    private WebTarget webTarget;*/


    @Inject
    @DiscoverService(value = "scores-service", environment = "dev", version = "1.0.0")
    private String scoresUrlString;

    @GET
    @Path("{gameId}/leaderboard")
    public Response viewLeaderboardForGame(@PathParam("gameId") String gameId) {

        System.out.println("=====================");

        StringBuilder response = new StringBuilder();

        response.append("{\"list\":");

        try {
            URL url = new URL(scoresUrlString + "/v1/scores");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.append("}");

        System.out.println(response.toString());
        ArrayList<String[]> leaderboard = new ArrayList<>();

        try {

            Object obj = JSONValue.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj;

            // loop array
            JSONArray scores = (JSONArray) jsonObject.get("list");


            for (int i = 0; i < scores.size(); i++) {
                System.out.println("===================");
                JSONObject score = (JSONObject) scores.get(i);
                if (score.get("gameId").equals(gameId)) {
                    // Get player ids
                    String id1 = (String) score.get("user1Id");
                    String id2 = (String) score.get("user2Id");
                    // Add players to the leaderboard
                    addPlayers(leaderboard, id1);
                    addPlayers(leaderboard, id2);
                    // Get player indices in the leaderboard arraylist
                    int index1 = userExists(leaderboard, id1);
                    int index2 = userExists(leaderboard, id2);
                    // Get the score of the match
                    long score1 = (long) score.get("user1Score");
                    long score2 = (long) score.get("user2Score");
                    // Add point on the leaderboard
                    if (score1 > score2) {
                        addPoints(leaderboard, index1, 3);
                    } else if (score2 > score1) {
                        addPoints(leaderboard, index2, 3);
                    } else if (score1 == score2) {
                        addPoints(leaderboard, index1, 1);
                        addPoints(leaderboard, index2, 1);
                    }

                }
                printLeaderboard(leaderboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=====================");

        System.out.println(buildLeaderboardJson(leaderboard));

        return Response.ok(buildLeaderboardJson(leaderboard)).build();
    }

    private ArrayList<String[]> addPlayers(ArrayList<String[]> leaderboard, String userId) {
        if (userExists(leaderboard, userId) == -1) {
            String[] newUser = new String[]{userId, "0"};
            leaderboard.add(newUser);
        }
        return leaderboard;
    }

    private int userExists(ArrayList<String[]> leaderboard, String playerId) {

        for (int i = 0; i < leaderboard.size(); i++) {
            String[] player = leaderboard.get(i);
            if (player[0].equals(playerId)) {
                return i;
            }
        }

        return -1;
    }

    private ArrayList<String[]> addPoints(ArrayList<String[]> leaderboard, int index, int points) {
        String userId = leaderboard.get(index)[0];
        String newPoints = Integer.toString(Integer.parseInt(leaderboard.get(index)[1]) + points);
        leaderboard.set(index, new String[]{userId, newPoints});
        return leaderboard;
    }

    private void printLeaderboard(ArrayList<String[]> leaderboard) {
        for (int i = 0; i < leaderboard.size(); i++) {
            System.out.println("Player " + leaderboard.get(i)[0] + " has " + leaderboard.get(i)[1] + " points.");
        }
    }

    private String buildLeaderboardJson(ArrayList<String[]> leaderboard) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < leaderboard.size(); i++) {
            result.append("{\"player\": \"" + leaderboard.get(i)[0] + "\", \"points\": " + leaderboard.get(i)[1] + "}");

            System.out.println("Index: " + (i + 1) + ", size: " + leaderboard.size());
            if(leaderboard.size() > i + 1) {
                result.append(",");
            }
        }
        result.append("]");
        return String.format(result.toString());
    }
}
