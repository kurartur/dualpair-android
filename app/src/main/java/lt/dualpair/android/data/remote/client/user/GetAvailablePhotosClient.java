package lt.dualpair.android.data.remote.client.user;

import java.util.List;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.ui.accounts.AccountType;
import retrofit2.Retrofit;
import rx.Observable;

public class GetAvailablePhotosClient extends BaseClient<List<Photo>> {

    private Long userId;
    private AccountType accountType;

    public GetAvailablePhotosClient(Long userId, AccountType accountType) {
        this.userId = userId;
        this.accountType = accountType;
    }

    @Override
    protected Observable<List<Photo>> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getAvailablePhotos(userId, accountType.name());
    }
}
