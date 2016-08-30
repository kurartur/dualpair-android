package lt.dualpair.android.services.device;

import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class RegisterDeviceClient extends BaseClient<Void> {

    private String token;

    public RegisterDeviceClient(String token) {
        this.token = token;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(DeviceService.class).registerDevice(token);
    }

}
