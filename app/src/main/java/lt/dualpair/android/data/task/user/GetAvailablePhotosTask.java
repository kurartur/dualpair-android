package lt.dualpair.android.data.task.user;

import android.content.Context;

import java.util.List;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.user.GetAvailablePhotosClient;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.ui.accounts.AccountType;

public class GetAvailablePhotosTask extends AuthenticatedUserTask<List<Photo>> {

    private AccountType accountType;

    public GetAvailablePhotosTask(Context context, AccountType accountType) {
        super(context);
        this.accountType = accountType;
    }

    @Override
    protected List<Photo> run() throws Exception {
        return new GetAvailablePhotosClient(getUserId(), accountType).observable().toBlocking().first();
    }
}


