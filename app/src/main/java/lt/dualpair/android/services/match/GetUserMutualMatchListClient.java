package lt.dualpair.android.services.match;

import android.text.TextUtils;

import java.util.Date;

import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.ResourceCollection;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUserMutualMatchListClient extends BaseClient<ResourceCollection<Match>> {

    private String url;
    private Long userId;

    public GetUserMutualMatchListClient(String url) {
        this.url = url;
    }

    public GetUserMutualMatchListClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<ResourceCollection<Match>> getApiObserable(Retrofit retrofit) {
        MatchService matchService = retrofit.create(MatchService.class);
        if (!TextUtils.isEmpty(url)) {
            return matchService.getUserMutualMatches(url);
        } else {
            return matchService.getUserMutualMatches(userId, new Date().getTime()); // TODO timezone
        }
    }
}
