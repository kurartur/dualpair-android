package lt.dualpair.android.core.user;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.services.user.SetSearchParametersClient;

public class SetSearchParametersTask extends AuthenticatedUserTask<Void> {

    private SearchParameters searchParameters;

    public SetSearchParametersTask(Activity activity, SearchParameters searchParameters) {
        super(activity);
        this.searchParameters = searchParameters;
    }

    @Override
    protected Void run() throws Exception {
        return new SetSearchParametersClient(getUserId(), searchParameters).observable().toBlocking().first();
    }
}
