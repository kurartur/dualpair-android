package lt.dualpair.android.core.match;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.services.match.GetNextMatchClient;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    public GetNextMatchTask(Context context) {
        super(context);
    }

    @Override
    protected Match run() throws Exception {
        return new GetNextMatchClient().observable().toBlocking().first();
    }
}
