package lt.dualpair.android.data.remote.client.match;

import java.util.Date;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUserMatchListClient extends BaseClient<ResourceCollection<Match>> {

    public static final int MUTUAL = 1;
    public static final int REVIEWED = 2;

    private Long userId;
    private int type;

    public GetUserMatchListClient(Long userId, int type) {
        this.userId = userId;
        if (type != MUTUAL && type != REVIEWED) {
            throw new IllegalArgumentException("Invalid type");
        }
        this.type = type;
    }

    @Override
    protected Observable<ResourceCollection<Match>> getApiObserable(Retrofit retrofit) {
        MatchService matchService = retrofit.create(MatchService.class);
        if (type == REVIEWED) {
            return matchService.getUserReviewedMatches(userId, new Date().getTime()); // TODO timezone
        } else {
            return matchService.getUserMutualMatches(userId, new Date().getTime());
        }
    }
}
