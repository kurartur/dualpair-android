package lt.dualpair.android.core.match;

import android.app.Activity;

import java.util.List;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.services.match.GetMatchListClient;

public class GetMatchListTask extends AuthenticatedUserTask<List<Match>> {

    public GetMatchListTask(Activity activity) {
        super(activity);
    }

    @Override
    protected List<Match> run() throws Exception {
        return new GetMatchListClient().observable().toBlocking().first();
    }
}
