package lt.dualpair.android.core.user;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.User;
import lt.dualpair.android.services.user.GetUserPrincipalClient;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    public GetUserPrincipalTask(Context context) {
        super(context);
    }

    @Override
    protected User run() {
        return new GetUserPrincipalClient().observable().toBlocking().first();
    }
}
