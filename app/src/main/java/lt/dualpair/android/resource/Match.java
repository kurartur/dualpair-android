package lt.dualpair.android.resource;

import java.io.Serializable;

public class Match implements Serializable {

    private Long id;
    private MatchParty user;
    private MatchParty opponent;

    public Long getId() {
        return id;
    }

    public MatchParty getUser() {
        return user;
    }

    public MatchParty getOpponent() {
        return opponent;
    }

}
