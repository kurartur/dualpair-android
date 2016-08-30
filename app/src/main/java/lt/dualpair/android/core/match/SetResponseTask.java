package lt.dualpair.android.core.match;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.services.match.SetResponseClient;

public class SetResponseTask extends AuthenticatedUserTask<Match> {

    private Long matchId;
    private Response response;

    public SetResponseTask(Activity activity, Long matchId, Response response) {
        super(activity);
        this.matchId = matchId;
        this.response = response;
    }

    @Override
    protected Match run() throws Exception {
        return new SetResponseClient(matchId, response).observable().toBlocking().first();
    }
}
