package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.services.BaseClient;

import retrofit2.Retrofit;
import rx.Observable;

public class GetUserPrincipal extends BaseClient<User> {

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getUser();
    }
}
