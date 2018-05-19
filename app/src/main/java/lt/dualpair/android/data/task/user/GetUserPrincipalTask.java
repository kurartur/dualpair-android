package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    private static final int EXPIRATION_TIME_MS = 1000 * 60 * 5; // 5 minutes
    private boolean forceUpdate;

    public GetUserPrincipalTask() {
        this(false);
    }

    public GetUserPrincipalTask(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                User user = userRepository.get(AccountUtils.getUserId(context));
                if (user == null || isExpired(user.getUpdateTime()) || forceUpdate) {
                    user = new GetUserPrincipalClient().observable().toBlocking().first();
                    user.setUpdateTime(new Date());
                    fixRelationshipStatus(user);
                    userRepository.save(user);
                    subscriber.onNext(user);
                } else {
                    subscriber.onNext(user);
                }
                subscriber.onCompleted();
            }
        });
    }

    // TODO should be fixed on server side
    private void fixRelationshipStatus(User user) {
        if (user.getRelationshipStatus() == null) {
            user.setRelationshipStatus(RelationshipStatus.NONE);
        }
    }

    private boolean isExpired(Date updateTime) {
        return new Date().getTime() - updateTime.getTime() > EXPIRATION_TIME_MS;
    }
}
