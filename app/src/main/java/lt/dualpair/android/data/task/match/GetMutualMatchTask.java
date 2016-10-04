package lt.dualpair.android.data.task.match;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.match.GetMutualMatchClient;
import lt.dualpair.android.data.resource.Match;

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
