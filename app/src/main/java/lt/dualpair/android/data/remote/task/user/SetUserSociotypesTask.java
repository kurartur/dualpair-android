package lt.dualpair.android.data.remote.task.user;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.SetUserSociotypesClient;
import lt.dualpair.android.data.resource.Sociotype;

public class SetUserSociotypesTask extends AuthenticatedUserTask<Void> {

    private Set<String> codes = new HashSet<>();

    public SetUserSociotypesTask(Context context, Set<Sociotype> sociotypes) {
        super(context);
        for (Sociotype sociotype : sociotypes) {
            codes.add(sociotype.getCode1());
        }
    }

    @Override
    protected Void run() throws Exception {
        return new SetUserSociotypesClient(codes, getUserId()).observable().toBlocking().first();
    }
}
