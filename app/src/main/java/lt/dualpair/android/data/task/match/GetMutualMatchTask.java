package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.match.GetMutualMatchClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetMutualMatchTask extends AuthenticatedUserTask<Match> {

    private Long matchId;

    public GetMutualMatchTask(String authToken, Long matchId) {
        super(authToken);
        this.matchId = matchId;
    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(Subscriber<? super Match> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                MatchRepository matchRepository = new MatchRepository(db);
                UserRepository userRepository = new UserRepository(db);
                Match match = matchRepository.findOne(matchId, getUserId(context));
                if (match != null) {
                    subscriber.onNext(match);
                } else {
                    match = new GetMutualMatchClient(getUserId(context), matchId).observable().toBlocking().first();
                    match.getUser().setUser(userRepository.get(getUserId(context)));
                    matchRepository.save(match);
                    subscriber.onNext(match);
                }
                subscriber.onCompleted();
            }
        });
    }
}
