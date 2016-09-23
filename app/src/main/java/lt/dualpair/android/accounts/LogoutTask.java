package lt.dualpair.android.accounts;

import android.app.Activity;

import lt.dualpair.android.services.authentication.LogoutClient;

public class LogoutTask extends AuthenticatedUserTask<Void> {

    public LogoutTask(Activity activity) {
        super(activity);
    }

    @Override
    protected Void run() throws Exception {
        return new LogoutClient().observable().toBlocking().first();
    }
}
