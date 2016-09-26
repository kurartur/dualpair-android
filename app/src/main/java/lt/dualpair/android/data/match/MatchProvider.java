package lt.dualpair.android.data.match;

import android.content.Context;

import java.util.List;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.Provider;
import lt.dualpair.android.data.remote.task.match.GetNextMatchTask;
import lt.dualpair.android.data.remote.task.match.SetResponseTask;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MatchProvider extends Provider {

    private static Subject<Match, Match> globalSubject = PublishSubject.create();

    private MatchRepository matchRepository;

    public MatchProvider(Context context) {
        super(context);
        matchRepository = new MatchRepository(DbHelper.forCurrentUser(context).getWritableDatabase());
    }

    public Subscription next(Subscriber<Match> subscriber) {

        final Subject<Match, Match> subject = PublishSubject.create();
        Subscription subscription = subject.subscribe(subscriber);

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
                    matchRepository.save(match);
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
        Match match = matchRepository.one(matchId, getUserId());
        match.getUser().setResponse(response);
        matchRepository.setResponse(match.getUser().getId(), response);
        new SetResponseTask(context, match.getUser().getId(), response).execute(new EmptySubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                // TODO update row
            }
        });
        globalSubject.onNext(match);
    }

    private Long getUserId() {
        return AccountUtils.getUserId(context);
    }

}
