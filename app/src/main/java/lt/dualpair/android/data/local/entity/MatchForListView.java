package lt.dualpair.android.data.local.entity;

import java.util.List;

public class MatchForListView {

    private Match match;
    private User opponent;
    private List<UserAccount> opponentAccounts;
    private List<UserPhoto> opponentPhotos;

    public MatchForListView(Match match, User opponent, List<UserAccount> opponentAccounts, List<UserPhoto> opponentPhotos) {
        this.match = match;
        this.opponent = opponent;
        this.opponentAccounts = opponentAccounts;
        this.opponentPhotos = opponentPhotos;
    }

    public Match getMatch() {
        return match;
    }

    public User getOpponent() {
        return opponent;
    }

    public List<UserAccount> getOpponentAccounts() {
        return opponentAccounts;
    }

    public List<UserPhoto> getOpponentPhotos() {
        return opponentPhotos;
    }

}
