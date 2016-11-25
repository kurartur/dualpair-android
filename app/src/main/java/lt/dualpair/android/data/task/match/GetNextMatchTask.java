package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.dualpair.android.data.remote.client.match.GetNextMatchClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    private Integer minAge;
    private Integer maxAge;
    private Boolean searchFemale;
    private Boolean searchMale;

    public GetNextMatchTask(String authToken, Integer minAge, Integer maxAge, Boolean searchFemale, Boolean searchMale) {
        super(authToken);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchFemale = searchFemale;
        this.searchMale = searchMale;
    }

    @Override
    protected Observable<Match> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Match>() {
            @Override
            public void call(Subscriber<? super Match> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                MatchRepository matchRepository = new MatchRepository(db);
                UserRepository userRepository = new UserRepository(db);
                List<Match> matchList = matchRepository.next(getUserId(context));
                if (!matchList.isEmpty()) {
                    subscriber.onNext(matchList.get(0));
                } else {
                    Match match = new GetNextMatchClient(minAge, maxAge, searchFemale, searchMale).observable().toBlocking().first();
                    match.getUser().setUser(userRepository.get(getUserId(context)));
                    matchRepository.save(match);
                    subscriber.onNext(match);
                }
                subscriber.onCompleted();
            }
        });
    }

}
