package lt.dualpair.android.data.match;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.Provider;
import lt.dualpair.android.data.remote.SyncStatus;
import lt.dualpair.android.data.remote.task.match.GetNextMatchTask;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.user.SearchParametersRepository;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MatchProvider extends Provider {

    private static Subject<Match, Match> matchesSubjects = PublishSubject.create();

    private MatchRepository matchRepository;
    private SearchParametersRepository searchParametersRepository;

    public MatchProvider(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        matchRepository = new MatchRepository(db);
        searchParametersRepository = new SearchParametersRepository(db);
    }

    public Subscription next(Subscriber<Match> subscriber) {

        final Subject<Match, Match> subject = PublishSubject.create();
        Subscription subscription = subject.subscribe(subscriber);

        List<Match> matchList = matchRepository.next(getUserId());
        if (!matchList.isEmpty()) {
            final Match match = matchList.get(0);
            matchesSubjects.filter(createFilter(match.getId()))
                    .subscribe(subject);
            subject.onNext(match);
        } else {
            SearchParameters sp = searchParametersRepository.getLastUsed();
            new GetNextMatchTask(context, sp.getMinAge(), sp.getMaxAge(), sp.getSearchFemale(), sp.getSearchMale()).execute(new EmptySubscriber<Match>() {
                @Override
                public void onError(Throwable e) {
                    subject.onError(e);
                }

                @Override
                public void onNext(Match match) {
                    matchRepository.save(match);
                    matchesSubjects.filter(createFilter(match.getId()))
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
        Match match = matchRepository.findOne(matchId, getUserId());
        match.getUser().setResponse(response);

        MatchParty matchParty = match.getUser();
        matchRepository.setResponse(matchParty.getId(), response);
        matchRepository.setMatchPartySyncStatus(matchParty.getId(), SyncStatus.UPDATE);

        matchesSubjects.onNext(match);

        ContentResolver.requestSync(AccountUtils.getAccount(context), null, null);
    }

    public void notifySubscribers(Match match) {
        matchesSubjects.onNext(match);
    }

    private Long getUserId() {
        return AccountUtils.getUserId(context);
    }

}
