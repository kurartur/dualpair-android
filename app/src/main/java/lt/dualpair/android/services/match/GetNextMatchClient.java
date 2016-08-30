package lt.dualpair.android.services.match;

import lt.dualpair.android.resource.Match;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class GetNextMatchClient extends BaseClient<Match> {

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).getNext();
    }
}
