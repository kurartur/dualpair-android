package lt.dualpair.android.services.socionics;

import java.util.Map;

import lt.dualpair.android.resource.Sociotype;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class EvaluateTestClient extends BaseClient<Sociotype> {

    private Map<String, String> choices;

    public EvaluateTestClient(Map<String, String> choices) {
        this.choices = choices;
    }

    @Override
    protected Observable<Sociotype> getApiObserable(Retrofit retrofit) {
        return retrofit.create(SocionicsService.class).evaluateTest(choices);
    }
}
