package lt.dualpair.android.core.user;

import android.app.Activity;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.User;
import lt.dualpair.android.services.user.GetUserPrincipalClient;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    public GetUserPrincipalTask(Activity activity) {
        super(activity);
    }

    @Override
    protected User run() {
        return new GetUserPrincipalClient().observable().toBlocking().first();
    }
}
