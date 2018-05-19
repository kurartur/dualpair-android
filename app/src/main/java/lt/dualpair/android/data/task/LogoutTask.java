package lt.dualpair.android.data.task;

import android.content.Context;

import lt.dualpair.android.data.remote.client.authentication.LogoutClient;
import rx.Observable;

public class LogoutTask extends AuthenticatedUserTask<Void> {

    @Override
    protected Observable<Void> run(Context context) {
        return new LogoutClient().observable();
    }

}
