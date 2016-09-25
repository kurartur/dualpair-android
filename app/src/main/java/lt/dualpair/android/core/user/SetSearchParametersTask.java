package lt.dualpair.android.core.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.services.user.SetSearchParametersClient;

public class SetSearchParametersTask extends AuthenticatedUserTask<Void> {

    private SearchParameters searchParameters;

    public SetSearchParametersTask(Context context, SearchParameters searchParameters) {
        super(context);
        this.searchParameters = searchParameters;
    }

    @Override
    protected Void run() throws Exception {
        return new SetSearchParametersClient(getUserId(), searchParameters).observable().toBlocking().first();
    }
}
