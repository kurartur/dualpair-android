package lt.dualpair.android.services.match;

import java.util.List;

import lt.dualpair.android.resource.Match;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class GetMatchListClient extends BaseClient<List<Match>> {

    @Override
    protected Observable<List<Match>> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).getList();
    }
}
