package lt.dualpair.android.data.remote.client.user;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.SearchParameters;
import retrofit2.Retrofit;
import rx.Observable;

public class SetSearchParametersClient extends BaseClient<Void> {

    private Long userId;
    private SearchParameters searchParameters;

    public SetSearchParametersClient(Long userId, SearchParameters searchParameters) {
        this.userId = userId;
        this.searchParameters = searchParameters;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSearchParameters(userId, searchParameters);
    }
}
