package lt.dualpair.android.data.remote.task.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.SetSearchParametersClient;
import lt.dualpair.android.data.resource.SearchParameters;

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
