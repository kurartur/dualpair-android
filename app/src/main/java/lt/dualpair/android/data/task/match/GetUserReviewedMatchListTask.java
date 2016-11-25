package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.text.TextUtils;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.remote.client.SimpleGetUrlClient;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;

public class GetUserReviewedMatchListTask extends AuthenticatedUserTask<ResourceCollection<Match>> {

    private String url;

    public GetUserReviewedMatchListTask(String authToken, String url) {
        super(authToken);
        this.url = url;
    }

    @Override
    protected Observable<ResourceCollection<Match>> run(Context context) {
        BaseClient<ResourceCollection<Match>> client;
        if (!TextUtils.isEmpty(url)) {
            client = new SimpleGetUrlClient<>(url);
        } else {
            client = new GetUserMatchListClient(getUserId(context), GetUserMatchListClient.REVIEWED);
        }
        return client.observable();
    }

}
