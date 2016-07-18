package com.artur.dualpair.android.services.match;

import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.services.BaseClient;

import retrofit2.Retrofit;
import rx.Observable;

public class GetNextMatchClient extends BaseClient<Match> {

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).getNext();
    }
}
