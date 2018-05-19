package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetUserMutualMatchListTask extends AuthenticatedUserTask<Match> {

    private int start;
    private int count;
    private boolean refresh;

    public GetUserMutualMatchListTask(int start, int count, boolean refresh) {
        this.start = start;
        this.count = count;
        this.refresh = refresh;
    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(Subscriber<? super Match> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                MatchRepository matchRepository = new MatchRepository(db);
                UserRepository userRepository = new UserRepository(db);
                // TODO find matches in db first and use start and count
                ResourceCollection<Match> rc  = new GetUserMatchListClient(getUserId(context), GetUserMatchListClient.MUTUAL).observable().toBlocking().first();

                User principal = userRepository.get(getUserId(context));
                if (principal == null) {
                    principal = new GetUserPrincipalClient().observable().toBlocking().first();
                }

                for (Match match : rc.getContent()) {
                    match.getUser().setUser(principal);
                    matchRepository.save(match);
                    subscriber.onNext(match);
                }

                subscriber.onCompleted();
            }
        });
    }

}
