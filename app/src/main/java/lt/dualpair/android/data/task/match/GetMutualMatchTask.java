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
    private boolean refresh;

    public GetMutualMatchTask(Long matchId) {
        this.matchId = matchId;
    }

    public GetMutualMatchTask(Long matchId, boolean refresh) {
        this(matchId);
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

                Long userId = getUserId(context);

                Match match;

                if (refresh) {
                    match = loadRemotely(matchRepository, userRepository, matchId, userId);
                } else {
                    match = loadFromRepo(matchRepository, matchId, userId);
                    if (match == null) {
                        match = loadRemotely(matchRepository, userRepository, matchId, userId);
                    }
                }

                if (match != null) {
                    subscriber.onNext(match);
                } else {
                    subscriber.onError(new RuntimeException("Match not found"));
                }
                subscriber.onCompleted();
            }
        });
    }

    private Match loadFromRepo(MatchRepository matchRepository, Long matchId, Long userId) {
        return matchRepository.findOne(matchId, userId);
    }

    private Match loadRemotely(MatchRepository matchRepository, UserRepository userRepository,  Long matchId, Long userId) {
        Match match = new GetMutualMatchClient(userId, matchId).observable().toBlocking().first();
        match.getUser().setUser(userRepository.get(userId));
        matchRepository.save(match);
        return match;
    }
}
