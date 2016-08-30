package lt.dualpair.android.core.user;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.services.user.GetSearchParametersClient;

public class GetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    public GetSearchParametersTask(Activity activity) {
        super(activity);
    }

    @Override
    protected SearchParameters run() throws Exception {
        return new GetSearchParametersClient(getUserId()).observable().toBlocking().first();
    }
}
