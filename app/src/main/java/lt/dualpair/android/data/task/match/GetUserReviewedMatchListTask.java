package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.text.TextUtils;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.match.GetUserReviewedMatchListClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;

public class GetUserReviewedMatchListTask extends AuthenticatedUserTask<ResourceCollection<Match>> {

    private String url;

    public GetUserReviewedMatchListTask(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected ResourceCollection<Match> run() throws Exception {
        GetUserReviewedMatchListClient client;
        if (!TextUtils.isEmpty(url)) {
            client = new GetUserReviewedMatchListClient(url);
        } else {
            client = new GetUserReviewedMatchListClient(getUserId());
        }
        return client.observable().toBlocking().first();
    }
}
