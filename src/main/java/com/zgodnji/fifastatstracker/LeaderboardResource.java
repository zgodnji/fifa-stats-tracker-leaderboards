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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    public Response viewLeaderboardForGame() {

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
        try {

            Object obj = JSONValue.parse(response.toString());

            JSONObject jsonObject = (JSONObject) obj;
            System.out.println("-------------");
            System.out.println(jsonObject);

            /*String name = (String) jsonObject.get("name");
            System.out.println(name);*/

            System.out.println("--------------");
            // loop array
            JSONArray scores = (JSONArray) jsonObject.get("list");
            System.out.println(scores);

            for (int i = 0; i < scores.size(); i++) {
                System.out.println("++++++++++++");
                System.out.println(i);
                System.out.println(scores.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=====================");
        return Response.noContent().build();
    }
}
