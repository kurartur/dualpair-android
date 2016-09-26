package lt.dualpair.android.data.remote.task.user;

import android.app.Activity;

import java.util.Set;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.SetUserSociotypesClient;

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
