package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.dto.Location;
import com.artur.dualpair.android.services.BaseClient;

import retrofit2.Retrofit;
import rx.Observable;

public class SetLocationClient extends BaseClient<Void> {

    private Location location;

    public SetLocationClient(Location location) {
        this.location = location;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setLocation(location);
    }
}
