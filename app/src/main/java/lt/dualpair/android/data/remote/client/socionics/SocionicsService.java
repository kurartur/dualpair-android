package lt.dualpair.android.data.remote.client.socionics;

import java.util.Map;

import io.reactivex.Observable;
import lt.dualpair.android.data.resource.Sociotype;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SocionicsService {

    @FormUrlEncoded
    @POST("api/socionics/test/evaluate")
    Observable<Sociotype> evaluateTest(@FieldMap Map<String, String> choices);

}
