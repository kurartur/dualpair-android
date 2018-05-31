package lt.dualpair.android.data.remote.client.authentication;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import retrofit2.Retrofit;

public class RegisterDeviceClient extends CompletableClient {

    private String token;

    public RegisterDeviceClient(String token) {
        this.token = token;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(DeviceService.class).registerDevice(token);
    }
}
