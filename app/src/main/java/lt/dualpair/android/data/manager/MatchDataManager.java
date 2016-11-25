package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.ResourceCollection;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.match.GetMutualMatchTask;
import lt.dualpair.android.data.task.match.GetNextMatchTask;
import lt.dualpair.android.data.task.match.GetUserMutualMatchListTask;
import lt.dualpair.android.data.task.match.GetUserReviewedMatchListTask;
import lt.dualpair.android.data.task.match.SetResponseTask;
import rx.Observable;

public class MatchDataManager extends DataManager {

    private SearchParametersRepository searchParametersRepository;

    public MatchDataManager(Context context) {
        super(context);
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        searchParametersRepository = new SearchParametersRepository(db);
    }

    public Observable<Match> next() {
        final SearchParameters sp = searchParametersRepository.getLastUsed();
        return execute(context, new DataRequest<>("nextMatch", new AuthenticatedTaskCreator<Match>() {
            @Override
            protected Task<Match> doCreateTask(String authToken) {
                return new GetNextMatchTask(authToken, sp.getMinAge(), sp.getMaxAge(), sp.getSearchFemale(), sp.getSearchMale());
            }
        }));
    }

    public Observable<Match> setResponse(final Long matchId, final Response response) {
        return execute(context, new DataRequest<>("setResponse", new AuthenticatedTaskCreator<Match>() {
            @Override
            protected Task<Match> doCreateTask(String authToken) {
                return new SetResponseTask(authToken, matchId, response);
            }
        }));
    }

    public Observable<Match> match(final Long matchId) {
        return execute(context, new DataRequest<>("match" + matchId, new AuthenticatedTaskCreator<Match>() {
            @Override
            protected Task<Match> doCreateTask(String authToken) {
                return new GetMutualMatchTask(authToken, matchId);
            }
        }));
    }

    public Observable<ResourceCollection<Match>> mutualMatchList(final String url) {
        return execute(context, new DataRequest<>("mutualMatchList", new AuthenticatedTaskCreator<ResourceCollection<Match>>() {
            @Override
            protected Task<ResourceCollection<Match>> doCreateTask(String authToken) {
                return new GetUserMutualMatchListTask(authToken, url);
            }
        }));
    }

    public Observable<ResourceCollection<Match>> reviewedMatchList(final String url) {
        return execute(context, new DataRequest<>("reviewedMatchList", new AuthenticatedTaskCreator<ResourceCollection<Match>>() {
            @Override
            protected Task<ResourceCollection<Match>> doCreateTask(String authToken) {
                return new GetUserReviewedMatchListTask(authToken, url);
            }
        }));
    }
}
