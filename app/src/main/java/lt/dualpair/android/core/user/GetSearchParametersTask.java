package lt.dualpair.android.core.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.services.user.GetSearchParametersClient;

public class GetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    public GetSearchParametersTask(Context context) {
        super(context);
    }

    @Override
    protected SearchParameters run() throws Exception {
        return new GetSearchParametersClient(getUserId()).observable().toBlocking().first();
    }
}
