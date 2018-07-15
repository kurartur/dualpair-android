package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.User;
import retrofit2.Retrofit;

public class FindUserClient extends ObservableClient<User> {

    private Integer minAge;
    private Integer maxAge;
    private Boolean searchFemale;
    private Boolean searchMale;

    public FindUserClient(Integer minAge, Integer maxAge, Boolean searchFemale, Boolean searchMale) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchFemale = searchFemale;
        this.searchMale = searchMale;
    }

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class)
                .find(
                        minAge,
                        maxAge,
                        searchFemale ? "Y" : "N",
                        searchMale ? "Y" : "N");
    }
}
