package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.user.SetUserSociotypesClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class SetUserSociotypesTask extends AuthenticatedUserTask<User> {

    private Set<Sociotype> sociotypes;

    public SetUserSociotypesTask(String authToken, Set<Sociotype> sociotypes) {
        super(authToken);
        this.sociotypes = sociotypes;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                Set<String> codes = new HashSet<>();
                for (Sociotype sociotype : sociotypes) {
                    codes.add(sociotype.getCode1());
                }
                new SetUserSociotypesClient(codes, getUserId(context)).observable().toBlocking().first();
                User user = userRepository.get(AccountUtils.getUserId(context));
                user.setSociotypes(sociotypes);
                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }
}
