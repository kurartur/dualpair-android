package lt.dualpair.android.data.remote.task.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.GetSearchParametersClient;
import lt.dualpair.android.data.resource.SearchParameters;

public class GetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    public GetSearchParametersTask(Context context) {
        super(context);
    }

    @Override
    protected SearchParameters run() throws Exception {
        return new GetSearchParametersClient(getUserId()).observable().toBlocking().first();
    }
}
