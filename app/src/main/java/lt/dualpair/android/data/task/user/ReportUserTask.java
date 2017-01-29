package lt.dualpair.android.data.task.user;

import android.content.Context;

import lt.dualpair.android.data.remote.client.user.ReportUserClient;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;

public class ReportUserTask extends AuthenticatedUserTask<Void> {

    private Long userId;

    public ReportUserTask(String token, Long userId) {
        super(token);
        this.userId = userId;
    }

    @Override
    protected Observable<Void> run(Context context) {
        return new ReportUserClient(userId).observable();
    }
}
