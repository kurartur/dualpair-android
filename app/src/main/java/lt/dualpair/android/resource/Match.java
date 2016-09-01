package lt.dualpair.android.resource;

import java.io.Serializable;

public class Match implements Serializable {

    private Long id;
    private MatchParty user;
    private MatchParty opponent;
    private Response response;

    public Long getId() {
        return id;
    }

    public MatchParty getUser() {
        return user;
    }

    public MatchParty getOpponent() {
        return opponent;
    }

    public Response getResponse() {
        return response;
    }

    public boolean isMutual() {
        return user.getResponse() == Response.YES && opponent.getResponse() == Response.YES;
    }
}
