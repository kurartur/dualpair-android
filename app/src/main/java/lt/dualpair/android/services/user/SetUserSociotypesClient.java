package lt.dualpair.android.services.user;

import java.util.Set;

import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class SetUserSociotypesClient extends BaseClient<Void> {

    private Set<String> codes;
    private Long userId;

    public SetUserSociotypesClient(Set<String>  sociotypes, Long userId) {
        this.codes = sociotypes;
        this.userId = userId;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSociotypes(userId, codes);
    }
}
