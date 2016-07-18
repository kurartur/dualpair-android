package com.artur.dualpair.android.services.socionics;

import com.artur.dualpair.android.dto.Sociotype;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface SocionicsService {

    @FormUrlEncoded
    @POST("api/socionics/test/evaluate")
    Observable<Sociotype> evaluateTest(@FieldMap Map<String, String> choices);

}
