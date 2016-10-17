package lt.dualpair.android.data.remote.client.match;

import android.text.TextUtils;

import java.util.Date;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUserReviewedMatchListClient extends BaseClient<ResourceCollection<Match>> {

    private String url;
    private Long userId;

    public GetUserReviewedMatchListClient(String url) {
        this.url = url;
    }

    public GetUserReviewedMatchListClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<ResourceCollection<Match>> getApiObserable(Retrofit retrofit) {
        MatchService matchService = retrofit.create(MatchService.class);
        if (!TextUtils.isEmpty(url)) {
            return matchService.getUserReviewedMatches(url);
        } else {
            return matchService.getUserReviewedMatches(userId, new Date().getTime()); // TODO timezone
        }
    }
}
