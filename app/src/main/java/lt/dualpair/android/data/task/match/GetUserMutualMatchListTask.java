package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.text.TextUtils;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.match.GetUserMutualMatchListClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;

public class GetUserMutualMatchListTask extends AuthenticatedUserTask<ResourceCollection<Match>> {

    private String url;

    public GetUserMutualMatchListTask(Context context, String url) {
        super(context);
        this.url = url;
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
