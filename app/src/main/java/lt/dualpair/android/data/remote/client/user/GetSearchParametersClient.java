package lt.dualpair.android.data.remote.client.user;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.SearchParameters;
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
