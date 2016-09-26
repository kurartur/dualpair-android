package lt.dualpair.android.data.remote.services.match;

import lt.dualpair.android.data.remote.services.BaseClient;
import lt.dualpair.android.data.resource.Match;
import retrofit2.Retrofit;
import rx.Observable;

public class GetNextMatchClient extends BaseClient<Match> {

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).getNext();
    }
}
