package lt.dualpair.android.core.match;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.services.match.GetMutualMatchClient;

public class GetMutualMatchTask extends AuthenticatedUserTask<Match> {

    private Long matchId;

    public GetMutualMatchTask(Activity activity, Long matchId) {
        super(activity);
        this.matchId = matchId;
    }

    @Override
    protected Match run() throws Exception {
        return new GetMutualMatchClient(getUserId(), matchId).observable().toBlocking().first();
    }
}
