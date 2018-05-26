package lt.dualpair.android.data.local.entity;

import java.util.List;

public class Match {

    private Long id;
    private User opponent;
    private List<UserAccount> opponentAccounts;
    private List<UserPhoto> opponentPhotos;

    public Match(Long id, User opponent, List<UserAccount> opponentAccounts, List<UserPhoto> opponentPhotos) {
        this.id = id;
        this.opponent = opponent;
        this.opponentAccounts = opponentAccounts;
        this.opponentPhotos = opponentPhotos;
    }

    public Long getId() {
        return id;
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
