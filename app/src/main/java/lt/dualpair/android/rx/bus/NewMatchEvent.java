package lt.dualpair.android.rx.bus;

import lt.dualpair.android.resource.Match;

public class NewMatchEvent extends Event {

    private Match match;

    public NewMatchEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
