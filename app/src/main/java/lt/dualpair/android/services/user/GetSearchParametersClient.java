package lt.dualpair.android.services.user;

import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class GetSearchParametersClient extends BaseClient<SearchParameters> {

    private Long userId;

    public GetSearchParametersClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<SearchParameters> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getSearchParameters(userId);
    }
}
