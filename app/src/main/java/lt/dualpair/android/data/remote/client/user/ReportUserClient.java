package lt.dualpair.android.data.remote.client.user;

import java.util.HashMap;
import java.util.Map;

import lt.dualpair.android.data.remote.client.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class ReportUserClient extends BaseClient<Void> {

    private Long userId;

    public ReportUserClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        return retrofit.create(UserService.class).reportUser(data);
    }
}
