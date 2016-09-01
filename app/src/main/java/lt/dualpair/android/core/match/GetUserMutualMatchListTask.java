package lt.dualpair.android.core.match;

import android.app.Activity;
import android.text.TextUtils;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.ResourceCollection;
import lt.dualpair.android.services.match.GetUserMutualMatchListClient;

public class GetUserMutualMatchListTask extends AuthenticatedUserTask<ResourceCollection<Match>> {

    private String url;

    public GetUserMutualMatchListTask(Activity activity, String url) {
        super(activity);
        this.url = url;
    }

    public GetUserMutualMatchListTask(Activity activity) {
        super(activity);
    }

    @Override
    protected ResourceCollection<Match> run() throws Exception {
        GetUserMutualMatchListClient client;
        if (!TextUtils.isEmpty(url)) {
            client = new GetUserMutualMatchListClient(url);
        } else {
            client = new GetUserMutualMatchListClient(getUserId());
        }
        return client.observable().toBlocking().first();
    }
}
