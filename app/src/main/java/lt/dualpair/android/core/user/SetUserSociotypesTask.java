package lt.dualpair.android.core.user;

import android.app.Activity;

import java.util.Set;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.services.user.SetUserSociotypesClient;

public class SetUserSociotypesTask extends AuthenticatedUserTask<Void> {

    private Set<String> codes;

    public SetUserSociotypesTask(Activity activity, Set<String> codes) {
        super(activity);
        this.codes = codes;
    }

    @Override
    protected Void run() throws Exception {
        return new SetUserSociotypesClient(codes, getUserId()).observable().toBlocking().first();
    }
}
