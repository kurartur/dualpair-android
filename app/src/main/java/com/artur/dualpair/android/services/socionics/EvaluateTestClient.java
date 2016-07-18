package com.artur.dualpair.android.services.socionics;

import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.services.BaseClient;

import java.util.Map;

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
