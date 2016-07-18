package com.artur.dualpair.android.core.user;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.services.user.SetSearchParametersClient;

public class SetSearchParametersTask extends AuthenticatedUserTask<Void> {

    private SearchParameters searchParameters;

    public SetSearchParametersTask(Activity activity, SearchParameters searchParameters) {
        super(activity);
        this.searchParameters = searchParameters;
    }

    @Override
    protected Void run() throws Exception {
        return new SetSearchParametersClient(searchParameters).observable().toBlocking().first();
    }
}
