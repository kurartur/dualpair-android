package lt.dualpair.android.data.remote.services.match;

import lt.dualpair.android.data.remote.services.BaseClient;
import lt.dualpair.android.data.resource.Match;
import retrofit2.Retrofit;
import rx.Observable;

public class GetNextMatchClient extends BaseClient<Match> {

    private Integer minAge;
    private Integer maxAge;
    private Boolean searchFemale;
    private Boolean searchMale;

    public GetNextMatchClient(Integer minAge, Integer maxAge, Boolean searchFemale, Boolean searchMale) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchFemale = searchFemale;
        this.searchMale = searchMale;
    }

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class)
                .getNext(
                        minAge,
                        maxAge,
                        searchFemale ? "Y" : "N",
                        searchMale ? "Y" : "N");
    }
}
