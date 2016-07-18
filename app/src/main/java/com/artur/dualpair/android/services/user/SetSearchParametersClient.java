package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.services.BaseClient;

import retrofit2.Retrofit;
import rx.Observable;

public class SetSearchParametersClient extends BaseClient<Void> {

    private SearchParameters searchParameters;

    public SetSearchParametersClient(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setSearchParameters(searchParameters);
    }
}
