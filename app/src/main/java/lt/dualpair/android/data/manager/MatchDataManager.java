package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.match.GetMutualMatchTask;
import lt.dualpair.android.data.task.match.GetNextMatchTask;
import lt.dualpair.android.data.task.match.SetResponseTask;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MatchDataManager extends DataManager {

    private static Subject<Match, Match> matchesSubjects = PublishSubject.create();

    private SearchParametersRepository searchParametersRepository;

    public MatchDataManager(Context context) {
        super(context);
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
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

    public Observable<Match> setResponse(final Long matchId, final Response response) {
        final PublishSubject<Match> subject = PublishSubject.create();
        subject.doOnNext(new Action1<Match>() {
            @Override
            public void call(Match match) {
                matchesSubjects.filter(createFilter(match.getId()))
                        .subscribe(subject);
            }
        });
        enqueueTask(new QueuedTask<>("setResponse", new TaskCreator<Match>() {
            @Override
            public Task<Match> createTask(Context context) {
                return new SetResponseTask(context, matchId, response);
            }
        }, subject));
        return subject.asObservable();
    }

    public Observable<Match> match(final Long matchId) {
        final PublishSubject<Match> subject = PublishSubject.create();
        matchesSubjects.filter(new Func1<Match, Boolean>() {
            @Override
            public Boolean call(Match match) {
                return match.getId().equals(matchId);
            }
        }).subscribe(subject);
        enqueueTask(new QueuedTask("match" + matchId, new TaskCreator() {
            @Override
            public Task createTask(Context context) {
                return new GetMutualMatchTask(context, matchId);
            }
        }, subject));
        return subject.asObservable();
    }
}
