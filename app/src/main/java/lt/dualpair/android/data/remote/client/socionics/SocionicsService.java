package lt.dualpair.android.data.remote.client.socionics;

import java.util.Map;

import lt.dualpair.android.data.resource.Sociotype;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface SocionicsService {

    @FormUrlEncoded
    @POST("api/socionics/test/evaluate")
    Observable<Sociotype> evaluateTest(@FieldMap Map<String, String> choices);

}
