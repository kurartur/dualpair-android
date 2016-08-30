package lt.dualpair.android.services.device;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface DeviceService {

    @POST("api/device")
    @FormUrlEncoded
    Observable<Void> registerDevice(@Field("id") String token);

}
