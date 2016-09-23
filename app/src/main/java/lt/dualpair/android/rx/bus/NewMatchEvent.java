package lt.dualpair.android.rx.bus;

public class NewMatchEvent extends Event {

    private Long matchId;

    public NewMatchEvent(Long matchId) {
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }
}
