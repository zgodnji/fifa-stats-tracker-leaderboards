package com.zgodnji.fifastatstracker;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
}
