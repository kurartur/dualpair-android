package lt.dualpair.android.data.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.core.match.GetNextMatchTask;
import lt.dualpair.android.core.match.SetResponseTask;
import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.data.Provider;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.rx.EmptySubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MatchProvider extends Provider {

    private static Subject<Match, Match> globalSubject = PublishSubject.create();

    public MatchProvider(Context context) {
        super(context);
    }

    public Subscription next(Subscriber<Match> subscriber) {

        final Subject<Match, Match> subject = PublishSubject.create();
        Subscription subscription = subject.subscribe(subscriber);

        final MatchRepository matchRepository = new MatchRepository(getDbHelper().getWritableDatabase());
        List<Match> matchList = matchRepository.next(getUserId());
        if (!matchList.isEmpty()) {
            final Match match = matchList.get(0);
            globalSubject.filter(createFilter(match.getId()))
                    .subscribe(subject);
            subject.onNext(match);
        } else {
            new GetNextMatchTask(context).execute(new EmptySubscriber<Match>() {
                @Override
                public void onError(Throwable e) {
                    subject.onError(e);
                }

                @Override
                public void onNext(Match match) {
                    matchRepository.save(match, getUserId());
                    globalSubject.filter(createFilter(match.getId()))
                            .subscribe(subject);
                    subject.onNext(match);
                }
            });
        }
        return subscription;
    }

    private Func1<Match, Boolean> createFilter(final Long matchId) {
        return new Func1<Match, Boolean>() {
            @Override
            public Boolean call(Match m) {
                return m.getId().equals(matchId);
            }
        };
    }

    public void setResponse(Long matchId, Response response) {

        DbHelper dbHelper = DbHelper.forCurrentUser(context);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final Long userId = AccountUtils.getUserId(context);

        MatchRepository repository = new MatchRepository(db);
        Match match = repository.one(matchId, userId);
        match.getUser().setResponse(response);
        repository.setResponse(match.getUser().getId(), response);

        new SetResponseTask(context, match.getUser().getId(), response).execute(new EmptySubscriber<Void>() {

        });

        //globalSubject.onNext(match);
    }

    private DbHelper getDbHelper() {
        return DbHelper.forCurrentUser(context);
    }

    private Long getUserId() {
        return AccountUtils.getUserId(context);
    }

}
