package lt.dualpair.android.data.remote.client.user;

import java.util.List;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.Photo;
import lt.dualpair.android.ui.accounts.AccountType;
import retrofit2.Retrofit;

public class GetAvailablePhotosClient extends ObservableClient<List<Photo>> {

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
