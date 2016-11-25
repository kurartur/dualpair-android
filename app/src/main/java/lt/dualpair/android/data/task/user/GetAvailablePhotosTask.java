package lt.dualpair.android.data.task.user;

import android.content.Context;

import java.util.List;

import lt.dualpair.android.data.remote.client.user.GetAvailablePhotosClient;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import lt.dualpair.android.ui.accounts.AccountType;
import rx.Observable;

public class GetAvailablePhotosTask extends AuthenticatedUserTask<List<Photo>> {

    private AccountType accountType;

    public GetAvailablePhotosTask(String authToken, AccountType accountType) {
        super(authToken);
        this.accountType = accountType;
    }

    @Override
    protected Observable<List<Photo>> run(Context context) {
        return new GetAvailablePhotosClient(getUserId(context), accountType).observable();
    }

}


