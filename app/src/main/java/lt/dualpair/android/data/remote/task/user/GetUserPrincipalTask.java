package lt.dualpair.android.data.remote.task.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.GetUserPrincipalClient;
import lt.dualpair.android.data.resource.User;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    public GetUserPrincipalTask(Context context) {
        super(context);
    }

    @Override
    protected User run() {
        return new GetUserPrincipalClient().observable().toBlocking().first();
    }
}
