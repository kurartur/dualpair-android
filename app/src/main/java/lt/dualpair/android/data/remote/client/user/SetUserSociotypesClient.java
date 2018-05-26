package lt.dualpair.android.data.remote.client.user;

import java.util.Set;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.BaseClient;
import retrofit2.Retrofit;

public class SetUserSociotypesClient extends BaseClient<Void> {

    private Set<String> codes;
    private Long userId;

    public SetUserSociotypesClient(Long userId, Set<String> sociotypes) {
        this.codes = sociotypes;
        this.userId = userId;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSociotypes(userId, codes);

    }
}
