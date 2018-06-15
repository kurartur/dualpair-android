package lt.dualpair.android.data.remote.resource;

import java.io.Serializable;

public class Match implements Serializable {

    private Long id;
    private MatchParty user;
    private MatchParty opponent;
    private int distance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchParty getUser() {
        return user;
    }

    public void setUser(MatchParty user) {
        this.user = user;
    }

    public MatchParty getOpponent() {
        return opponent;
    }

    public void setOpponent(MatchParty opponent) {
        this.opponent = opponent;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isMutual() {
        return user.getResponse() == Response.YES && opponent.getResponse() == Response.YES;
    }
}
