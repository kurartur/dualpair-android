package lt.dualpair.android.data.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.SyncStatus;
import lt.dualpair.android.data.remote.task.match.GetNextMatchTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MatchDataManager extends DataManager {

    private static Subject<Match, Match> matchesSubjects = PublishSubject.create();

    private MatchRepository matchRepository;
    private SearchParametersRepository searchParametersRepository;

    public MatchDataManager(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        matchRepository = new MatchRepository(db);
        searchParametersRepository = new SearchParametersRepository(db);
    }

    public Observable<Match> next() {
        final PublishSubject<Match> subject = PublishSubject.create();
        List<Match> matchList = matchRepository.next(getUserId());
        if (!matchList.isEmpty()) {
            final Match match = matchList.get(0);
            matchesSubjects.filter(createFilter(match.getId()))
                    .subscribe(subject);
            subject.onNext(match);
        } else {
            SearchParameters sp = searchParametersRepository.getLastUsed();
            Task<Match> task = new GetNextMatchTask(context, sp.getMinAge(), sp.getMaxAge(), sp.getSearchFemale(), sp.getSearchMale());
            enqueueTask(new QueuedTask<>("nextMatch", task, new EmptySubscriber<Match>() {
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
            }));
        }
        return subject.asObservable();
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
