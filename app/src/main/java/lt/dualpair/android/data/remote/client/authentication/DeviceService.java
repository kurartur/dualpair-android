package lt.dualpair.android.data.remote.client.authentication;

import io.reactivex.Completable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeviceService {

    @POST("api/device")
    @FormUrlEncoded
    Completable registerDevice(@Field("id") String token);

}
