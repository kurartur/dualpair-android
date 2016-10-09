package lt.dualpair.android.data.task.match;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.match.GetMutualMatchClient;
import lt.dualpair.android.data.resource.Match;

public class GetMutualMatchTask extends AuthenticatedUserTask<Match> {

    private Long matchId;

    public GetMutualMatchTask(Context context, Long matchId) {
        super(context);
        this.matchId = matchId;
    }

    @Override
    protected Match run() throws Exception {
        return new GetMutualMatchClient(getUserId(), matchId).observable().toBlocking().first();
    }
}
