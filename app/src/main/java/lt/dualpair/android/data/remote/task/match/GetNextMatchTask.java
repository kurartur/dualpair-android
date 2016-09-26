package lt.dualpair.android.data.remote.task.match;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.match.GetNextMatchClient;
import lt.dualpair.android.data.resource.Match;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    public GetNextMatchTask(Context context) {
        super(context);
    }

    @Override
    protected Match run() throws Exception {
        return new GetNextMatchClient().observable().toBlocking().first();
    }
}
