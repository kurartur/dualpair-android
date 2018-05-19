package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
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

    public Observable<Match> next(SearchParameters sp) {
        return new GetNextMatchTask(sp.getMinAge(), sp.getMaxAge(), sp.getSearchFemale(), sp.getSearchMale()).execute(context);
    }

    public Observable<Match> setResponse(final Long matchId, final Response response) {
        return new SetResponseTask(matchId, response).execute(context);
    }

    public Observable<Match> match(final Long matchId) {
        return new GetMutualMatchTask(matchId).execute(context);
    }

    public Observable<Match> match(final Long matchId, final boolean refresh) {
        return new GetMutualMatchTask(matchId, refresh).execute(context);
    }

    public Observable<Match> mutualMatches(final Integer start, final Integer count) {
        return new GetUserMutualMatchListTask(start, count, true).execute(context);
    }

    public Observable<Match> historyMatches(final Integer start, final Integer count) {
        return new GetUserReviewedMatchListTask(start, count, true).execute(context);
    }
}
