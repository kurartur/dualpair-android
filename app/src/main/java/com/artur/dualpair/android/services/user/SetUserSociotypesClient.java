package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.services.BaseClient;

import java.util.Set;

import retrofit2.Retrofit;
import rx.Observable;

public class SetUserSociotypesClient extends BaseClient<Void> {

    private Set<Sociotype> sociotypes;

    public SetUserSociotypesClient(Set<Sociotype> sociotypes) {
        this.sociotypes = sociotypes;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSociotypes(sociotypes);
    }
}
