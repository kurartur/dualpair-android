package com.artur.dualpair.android.services.match;

import com.artur.dualpair.android.dto.Match;

import retrofit2.http.GET;
import rx.Observable;

public interface MatchService {

    @GET("/api/match/next")
    Observable<Match> getNext();

}
