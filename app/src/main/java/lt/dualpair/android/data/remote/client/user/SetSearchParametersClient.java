package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import lt.dualpair.android.data.remote.resource.SearchParameters;
import retrofit2.Retrofit;

public class SetSearchParametersClient extends CompletableClient {

    private Long userId;
    private SearchParameters searchParameters;

    public SetSearchParametersClient(Long userId, SearchParameters searchParameters) {
        this.userId = userId;
        this.searchParameters = searchParameters;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSearchParameters(userId, searchParameters);
    }
}
