package lt.dualpair.android.data.remote.client.user;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import retrofit2.Retrofit;

public class ReportUserClient extends CompletableClient {

    private Long userId;

    public ReportUserClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        return retrofit.create(UserService.class).reportUser(data);
    }
}
