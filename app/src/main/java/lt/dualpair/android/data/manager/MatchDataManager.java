package lt.dualpair.android.data.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.SyncStatus;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.match.GetNextMatchTask;
import rx.Observable;
import rx.functions.Action1;
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
        subject.doOnNext(new Action1<Match>() {
            @Override
            public void call(Match match) {
                matchesSubjects.filter(createFilter(match.getId()))
                        .subscribe(subject);
            }
        });
        final SearchParameters sp = searchParametersRepository.getLastUsed();
        enqueueTask(new QueuedTask<>("nextMatch", new TaskCreator<Match>() {
            @Override
            public Task<Match> createTask(Context context) {
                return new GetNextMatchTask(context, sp.getMinAge(), sp.getMaxAge(), sp.getSearchFemale(), sp.getSearchMale());
            }
        }, subject));
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

    private Long getUserId() {
        return AccountUtils.getUserId(context);
    }

}
